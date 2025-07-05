package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.mapper.HrObjectionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HrObjectionQueryServiceImpl implements HrObjectionQueryService {

    private final HrObjectionMapper mapper;

    // 인사 평가 이의제기 목록 조회
    @Override
    public HrObjectionListResultDto getObjections(Long empId, HrObjectionListRequestDto req) {
        log.info("[HrObjectionQueryServiceImpl] getObjections() 호출 - 요청: {}", req);

        // empId를 req에 넣어 HrObjectionListRequestDto를 빌더로 생성
        HrObjectionListRequestDto requestDto = HrObjectionListRequestDto.builder()
                .requesterEmpId(empId)
                .statusId(req.statusId())
                .roundNo(req.roundNo())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .page(req.page())
                .size(req.size())
                .build();


        // 전체 이의 제기 건수 조회
        long total = mapper.countObjections(requestDto);
        log.info("이의 제기 총 건수: {}", total);

        // 페이징 조회
        List<HrObjectionItemDto> list = mapper.findObjections(requestDto);
        if (list == null) {
            log.error("이의제기 목록을 찾을 수 없음 - 요청: {}", requestDto);
            throw new HrException(ErrorCode.HR_OBJECTIONS_NOT_FOUND);
        }

        // Pagination 생성
        Pagination pagination = buildPagination(req.page(), total);
        log.info("페이지네이션 정보 - currentPage: {}, totalItems: {}", pagination.getCurrentPage(), pagination.getTotalItems());

        return new HrObjectionListResultDto(list, pagination);
    }

    // 인사 평가 이의 제기 상세 조회
    @Override
    public ObjectionDetailResultDto getObjectionDetail(Long objectionId) {
        log.info("[HrObjectionQueryServiceImpl] getObjectionDetail() 호출 - objectionId={}", objectionId);

        // 기본 상세 정보 조회
        ObjectionItemDto base = mapper.findObjectionDetail(objectionId);
        if (base == null) {
            log.error("이의제기 상세 정보를 찾을 수 없음 - objectionId={}", objectionId);
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }
        log.info("이의제기 상세 정보 조회 완료 - 결과 ID: {}", base.getResultId());

        Long resultId = base.getResultId();

        // 요인별 점수 조회
        List<FactorScoreDto> scores = mapper.findFactorScores(resultId);
        if (scores == null) {
            log.error("요인별 점수를 찾을 수 없음 - resultId={}", resultId);
            throw new HrException(ErrorCode.FACTOR_SCORES_NOT_FOUND);
        }

        // 가중치 정보 조회
        WeightInfo weightInfo = mapper.findWeightInfo(resultId);
        if (weightInfo == null) {
            log.error("가중치 정보를 찾을 수 없음 - resultId={}", resultId);
            throw new HrException(ErrorCode.WEIGHT_INFO_NOT_FOUND);
        }

        // 등급 비율 정보 조회
        RateInfo rateInfo = mapper.findRateInfo(resultId);
        if (rateInfo == null) {
            log.error("등급 비율 정보를 찾을 수 없음 - resultId={}", resultId);
            throw new HrException(ErrorCode.RATE_INFO_NOT_FOUND);
        }

        // 최종 결과 반환
        ObjectionDetailResultDto result = ObjectionDetailResultDto.builder()
                .itemDto(base)
                .factorScores(scores)
                .weightInfo(weightInfo)
                .rateInfo(rateInfo)
                .build();
        log.info("이의제기 상세 조회 완료 - objectionId={}", objectionId);

        return result;
    }

    // 페이지네이션 정보를 생성하는 메서드
    private Pagination buildPagination(int page, long total) {
        int totalPage = (int) Math.ceil((double) total / 10); // 기본 페이지 사이즈 10으로 설정
        return Pagination.builder()
                .currentPage(page)
                .totalPage(totalPage)
                .totalItems(total)
                .build();
    }
}
