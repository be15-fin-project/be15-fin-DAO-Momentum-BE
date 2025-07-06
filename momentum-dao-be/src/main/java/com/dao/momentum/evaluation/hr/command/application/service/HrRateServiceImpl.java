package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateCreateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrRateUpdateDTO;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrRate;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrRateRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HrRateServiceImpl implements HrRateService {

    private final HrRateRepository hrRateRepository;

    @Override
    @Transactional
    public void create(int roundId, HrRateCreateDTO dto) {
        log.info("[HrRateServiceImpl] create() 호출 시작 - roundId={}, 요청 파라미터={}", roundId, dto);

        // Rate 합이 100인지 검증
        validateRateSum(dto.rateS(), dto.rateA(), dto.rateB(), dto.rateC(), dto.rateD());

        HrRate rate = HrRate.builder()
                .roundId(roundId)
                .rateS(dto.rateS())
                .rateA(dto.rateA())
                .rateB(dto.rateB())
                .rateC(dto.rateC())
                .rateD(dto.rateD())
                .build();

        hrRateRepository.save(rate);
        log.info("HR Rate 생성 완료 - roundId={}, rateS={}, rateA={}, rateB={}, rateC={}, rateD={}",
                roundId, dto.rateS(), dto.rateA(), dto.rateB(), dto.rateC(), dto.rateD());
    }

    @Override
    @Transactional
    public void update(Integer roundId, HrRateUpdateDTO dto) {
        log.info("[HrRateServiceImpl] update() 호출 시작 - roundId={}, 요청 파라미터={}", roundId, dto);

        // Rate 합이 100인지 검증
        validateRateSum(dto.rateS(), dto.rateA(), dto.rateB(), dto.rateC(), dto.rateD());

        HrRate rate = hrRateRepository.findByRoundId(roundId)
                .orElseThrow(() -> {
                    log.error("HR Rate 정보 없음 - roundId={}", roundId);
                    return new HrException(ErrorCode.HR_RATE_NOT_FOUND);
                });

        rate.update(dto.rateS(), dto.rateA(), dto.rateB(), dto.rateC(), dto.rateD());
        log.info("HR Rate 수정 완료 - roundId={}, rateS={}, rateA={}, rateB={}, rateC={}, rateD={}",
                roundId, dto.rateS(), dto.rateA(), dto.rateB(), dto.rateC(), dto.rateD());
    }

    private void validateRateSum(int s, int a, int b, int c, int d) {
        int total = s + a + b + c + d;
        if (total != 100) {
            log.error("HR Rate 합계가 100이 아님 - rateS={}, rateA={}, rateB={}, rateC={}, rateD={}, total={}", s, a, b, c, d, total);
            throw new HrException(ErrorCode.HR_RATE_INVALID_SUM);
        }
        log.info("HR Rate 합계 검증 완료 - total={}", total);
    }

    @Override
    @Transactional
    public void deleteByRoundId(int roundId) {
        log.info("[HrRateServiceImpl] deleteByRoundId() 호출 시작 - roundId={}, 요청 파라미터={}", roundId, roundId);

        hrRateRepository.deleteByRoundId(roundId);

        log.info("HR Rate 삭제 완료 - roundId={}", roundId);
    }
}
