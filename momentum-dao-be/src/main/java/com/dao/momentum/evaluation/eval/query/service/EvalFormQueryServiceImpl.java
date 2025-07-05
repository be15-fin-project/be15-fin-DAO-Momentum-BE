package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormDetailResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvalFormPromptRaw;
import com.dao.momentum.evaluation.eval.query.mapper.EvalFormMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvalFormQueryServiceImpl implements EvalFormQueryService {

    private final EvalFormMapper evalFormMapper;

    @Override
    public EvalFormDetailResultDto getFormDetail(Integer formId, Integer roundId) {
        log.info("[EvalFormQueryServiceImpl] getFormDetail() 호출 시작 - formId={}, roundId={}", formId, roundId);

        List<EvalFormPromptRaw> rawList = evalFormMapper.findFormDetailByFormId(formId);

        // 폼 상세 정보가 없으면 예외 발생
        if (rawList.isEmpty()) {
            log.error("폼 상세 정보를 찾을 수 없습니다 - formId={}", formId);
            throw new EvalException(ErrorCode.EVALUATION_PROMPT_NOT_FOUND);
        }

        String formName = rawList.get(0).formName();

        log.info("폼 상세 정보 조회 완료 - formName={}", formName);

        // 폼의 프롬프트 항목들을 프로퍼티 ID별로 그룹화
        Map<Integer, List<EvalFormPromptRaw>> groupedByProperty = rawList.stream()
                .collect(Collectors.groupingBy(EvalFormPromptRaw::propertyId, LinkedHashMap::new, Collectors.toList()));

        // 그룹화된 프롬프트들을 FactorDto 형태로 변환
        List<EvalFormDetailResultDto.FactorDto> factorDtos = groupedByProperty.values().stream()
                .map(group -> {
                    EvalFormPromptRaw any = group.get(0);
                    List<EvalFormDetailResultDto.PromptDto> prompts = group.stream()
                            .map(r -> EvalFormDetailResultDto.PromptDto.builder()
                                    .content(r.content())
                                    .isPositive(Boolean.TRUE.equals(r.isPositive()))
                                    .build())
                            .collect(Collectors.toList());

                    return EvalFormDetailResultDto.FactorDto.builder()
                            .propertyName(any.propertyName())
                            .prompts(prompts)
                            .build();
                })
                .collect(Collectors.toList());

        EvalFormDetailResultDto result = EvalFormDetailResultDto.builder()
                .formName(formName)
                .factors(factorDtos)
                .build();

        log.info("폼 상세 조회 완료 - formName={}, factorCount={}", result.formName(), result.factors().size());
        return result;
    }
}
