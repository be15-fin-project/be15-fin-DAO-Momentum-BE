package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormDetailResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormPromptRaw;
import com.dao.momentum.evaluation.eval.query.mapper.EvalFormMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvalFormQueryServiceImpl implements EvalFormQueryService {

    private final EvalFormMapper evalFormMapper;

    @Override
    public EvalFormDetailResultDto getFormDetail(Integer formId, Integer roundId) {
        List<EvalFormPromptRaw> rawList = evalFormMapper.findFormDetailByFormId(formId);

        if (rawList.isEmpty()) {
            throw new IllegalArgumentException("해당 양식의 문항 정보를 찾을 수 없습니다.");
        }

        String formName = rawList.get(0).getFormName();

        Map<Integer, List<EvalFormPromptRaw>> groupedByProperty = rawList.stream()
                .collect(Collectors.groupingBy(EvalFormPromptRaw::getPropertyId, LinkedHashMap::new, Collectors.toList()));

        List<EvalFormDetailResultDto.FactorDto> factorDtos = groupedByProperty.values().stream()
                .map(group -> {
                    EvalFormPromptRaw any = group.get(0);
                    List<EvalFormDetailResultDto.PromptDto> prompts = group.stream()
                            .map(r -> EvalFormDetailResultDto.PromptDto.builder()
                                    .content(r.getContent())
                                    .isPositive(Boolean.TRUE.equals(r.getIsPositive()))
                                    .build())
                            .collect(Collectors.toList());

                    return EvalFormDetailResultDto.FactorDto.builder()
                            .propertyName(any.getPropertyName())
                            .prompts(prompts)
                            .build();
                })
                .collect(Collectors.toList());

        return EvalFormDetailResultDto.builder()
                .formName(formName)
                .factors(factorDtos)
                .build();
    }
}
