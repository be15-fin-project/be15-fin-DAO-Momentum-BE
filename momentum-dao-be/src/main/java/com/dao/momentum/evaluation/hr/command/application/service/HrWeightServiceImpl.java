package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightCreateDTO;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrWeightUpdateDTO;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrWeight;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrWeightRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HrWeightServiceImpl implements HrWeightService {

    private final HrWeightRepository hrWeightRepository;

    @Override
    @Transactional
    public void create(int roundId, HrWeightCreateDTO dto) {
        log.info("[HrWeightServiceImpl] create() 호출 시작 - roundId={}, dto={}, 요청 파라미터={}", roundId, dto, dto);

        // 가중치 합 검증
        validateWeightSum(dto.performWt(), dto.teamWt(), dto.attitudeWt(),
                dto.growthWt(), dto.engagementWt(), dto.resultWt());

        HrWeight weight = HrWeight.builder()
                .roundId(roundId)
                .performWt(dto.performWt())
                .teamWt(dto.teamWt())
                .attitudeWt(dto.attitudeWt())
                .growthWt(dto.growthWt())
                .engagementWt(dto.engagementWt())
                .resultWt(dto.resultWt())
                .build();

        hrWeightRepository.save(weight);
        log.info("HR Weight 생성 완료 - roundId={}, performWt={}, teamWt={}, attitudeWt={}, growthWt={}, engagementWt={}, resultWt={}",
                roundId, dto.performWt(), dto.teamWt(), dto.attitudeWt(), dto.growthWt(), dto.engagementWt(), dto.resultWt());
    }

    @Override
    @Transactional
    public void update(Integer roundId, HrWeightUpdateDTO dto) {
        log.info("[HrWeightServiceImpl] update() 호출 시작 - roundId={}, dto={}, 요청 파라미터={}",
                roundId, dto, dto);

        // 가중치 합 검증
        validateWeightSum(dto.performWt(), dto.teamWt(), dto.attitudeWt(),
                dto.growthWt(), dto.engagementWt(), dto.resultWt());

        HrWeight weight = hrWeightRepository.findByRoundId(roundId)
                .orElseThrow(() -> {
                    log.error("HR Weight 정보 없음 - roundId={}", roundId);
                    return new HrException(ErrorCode.HR_WEIGHT_NOT_FOUND);
                });

        weight.update(dto.performWt(), dto.teamWt(), dto.attitudeWt(),
                dto.growthWt(), dto.engagementWt(), dto.resultWt());

        log.info("HR Weight 수정 완료 - roundId={}, performWt={}, teamWt={}, attitudeWt={}, growthWt={}, engagementWt={}, resultWt={}",
                roundId, dto.performWt(), dto.teamWt(), dto.attitudeWt(), dto.growthWt(), dto.engagementWt(), dto.resultWt());
    }

    private void validateWeightSum(int perform, int team, int attitude,
                                   int growth, int engagement, int result) {
        int total = perform + team + attitude + growth + engagement + result;
        if (total != 100) {
            log.error("HR Weight 합계가 100이 아님 - perform={}, team={}, attitude={}, growth={}, engagement={}, result={}, total={}",
                    perform, team, attitude, growth, engagement, result, total);
            throw new HrException(ErrorCode.HR_WEIGHT_INVALID_SUM);
        }
        log.info("HR Weight 합계 검증 완료 - total={}", total);
    }

    @Override
    @Transactional
    public void deleteByRoundId(int roundId) {
        log.info("[HrWeightServiceImpl] deleteByRoundId() 호출 시작 - roundId={}, 요청 파라미터={}", roundId, roundId);

        hrWeightRepository.deleteByRoundId(roundId);
        log.info("HR Weight 삭제 완료 - roundId={}", roundId);
    }
}
