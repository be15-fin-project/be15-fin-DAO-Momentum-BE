package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionRaw;
import com.dao.momentum.evaluation.hr.query.mapper.MyObjectionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyObjectionQueryServiceImpl implements MyObjectionQueryService {

    private final MyObjectionMapper mapper;

    // 사원의 이의 제기 목록을 조회하는 메서드
    @Override
    public MyObjectionListResultDto getMyObjections(Long empId, MyObjectionListRequestDto req) {
        log.info("[MyObjectionQueryServiceImpl] getMyObjections() 호출 시작 - empId={}, req={}", empId, req);

        // 1) 전체 건수 조회
        long total = mapper.countMyObjections(empId, req);
        log.info("사원의 이의 제기 총 건수 조회 완료 - total={}", total);

        // 2) 이의 제기 목록 조회
        List<MyObjectionRaw> rawList = mapper.findMyObjections(empId, req);
        if (rawList == null || rawList.isEmpty()) {
            log.error("이의 제기 목록을 찾을 수 없음 - empId={}, req={}", empId, req);
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }
        log.info("이의 제기 목록 조회 완료 - rawList.size={}", rawList.size());

        // 3) 목록 처리 및 DTO 변환
        List<MyObjectionItemDto> content = rawList.stream()
                .map(raw -> MyObjectionItemDto.builder()
                        .objectionId(raw.getObjectionId())
                        .resultId(raw.getResultId())
                        .statusId(raw.getStatusId())
                        .roundNo(raw.getRoundNo())
                        .statusType(raw.getStatusType())
                        .createdAt(raw.getCreatedAt())
                        .overallGrade(toGrade(raw.getOverallScore()))
                        .build())
                .collect(Collectors.toList());

        // 4) 페이지네이션 정보 생성
        int totalPage = (int) Math.ceil((double) total / req.getSize());
        Pagination pagination = Pagination.builder()
                .currentPage(req.getPage())
                .totalItems(total)
                .totalPage(totalPage)
                .build();
        log.info("페이지네이션 정보 생성 완료 - currentPage={}, totalPage={}, totalItems={}",
                pagination.getCurrentPage(), pagination.getTotalPage(), pagination.getTotalItems());

        return new MyObjectionListResultDto(content, pagination);
    }

    // 점수를 등급으로 변환하는 메서드
    private String toGrade(int score) {
        if (score >= 95) return "S";
        if (score >= 85) return "A";
        if (score >= 75) return "B";
        if (score >= 60) return "C";
        return "D";
    }

    // 이의 제기 상세 내역을 조회하는 메서드
    @Override
    public ObjectionDetailResultDto getObjectionDetail(Long objectionId) {
        log.info("[MyObjectionQueryServiceImpl] getObjectionDetail() 호출 시작 - objectionId={}", objectionId);

        // 1) 기본 상세 정보 조회
        ObjectionItemDto base = mapper.findObjectionDetail(objectionId);
        if (base == null) {
            log.error("이의 제기 상세 정보를 찾을 수 없음 - objectionId={}", objectionId);
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }
        log.info("이의 제기 기본 정보 조회 완료 - objectionId={}", objectionId);

        Long resultId = base.getResultId();
        log.info("결과 ID 조회 완료 - resultId={}", resultId);

        // 2) 요인별 점수 조회
        List<FactorScoreDto> scores = mapper.findFactorScores(resultId);
        log.info("요인별 점수 조회 완료 - resultId={}, scores.size={}", resultId, scores.size());

        // 3) 가중치 정보 조회
        WeightInfo weightInfo = mapper.findWeightInfo(resultId);
        log.info("가중치 정보 조회 완료 - resultId={}", resultId);

        // 4) 등급 비율 정보 조회
        RateInfo rateInfo = mapper.findRateInfo(resultId);
        log.info("등급 비율 정보 조회 완료 - resultId={}", resultId);

        // 5) 상세 내역 조립 후 반환
        ObjectionDetailResultDto result = ObjectionDetailResultDto.builder()
                .itemDto(base)
                .factorScores(scores)
                .weightInfo(weightInfo)
                .rateInfo(rateInfo)
                .build();
        log.info("이의 제기 상세 조회 완료 - objectionId={}", objectionId);

        return result;
    }

}
