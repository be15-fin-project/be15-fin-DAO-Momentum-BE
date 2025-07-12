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
import java.util.Comparator;
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
        log.info("[MyObjectionQueryServiceImpl] getMyObjections() 호출 - empId={}, req={}", empId, req);

        // 1) 전체 건수 조회
        long total = mapper.countMyObjections(empId, req);
        log.info("사원의 이의 제기 총 건수 조회 - total={}", total);

        // 2) 이의 제기 목록 조회
        List<MyObjectionRaw> rawList = mapper.findMyObjections(empId, req);
        if (rawList == null) {
            log.error("이의 제기 목록을 찾을 수 없음 - empId={}, req={}", empId, req);
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }

        // 3) resultId 리스트 추출
        List<Long> resultIds = rawList.stream()
                .map(MyObjectionRaw::resultId)
                .collect(Collectors.toList());

        // 4) 각 resultId에 대한 rateInfo, 전체 점수 리스트 조회
        // -> mapper 단에서 score + rateInfo + 전체 리스트까지 가져오는 구조로 통합하거나, 필요시 개별 조회
        List<MyObjectionItemDto> content = rawList.stream()
                .map(raw -> {
                    RateInfo rateInfo = mapper.findRateInfo(raw.resultId());
                    List<Integer> allScores = mapper.findAllScores(raw.resultId());

                    String grade = calculateRelativeGrade(allScores, raw.overallScore(), rateInfo);

                    return MyObjectionItemDto.builder()
                            .objectionId(raw.objectionId())
                            .resultId(raw.resultId())
                            .statusId(raw.statusId())
                            .roundNo(raw.roundNo())
                            .statusType(raw.statusType())
                            .createdAt(raw.createdAt())
                            .overallGrade(grade)
                            .build();
                })
                .collect(Collectors.toList());

        log.info("이의 제기 목록 처리 완료 - 목록 항목 수={}", content.size());

        // 5) 페이지네이션 생성
        Pagination pagination = buildPagination(req.page(), total);

        return new MyObjectionListResultDto(content, pagination);
    }

    // 점수를 등급으로 변환하는 메서드
    private String calculateRelativeGrade(List<Integer> allScores, int myScore, RateInfo rateInfo) {
        if (allScores == null || allScores.isEmpty()) return "-";

        // 점수 높은 순으로 정렬
        List<Integer> sorted = allScores.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        int rank = sorted.indexOf(myScore) + 1;
        int total = sorted.size();
        double percentile = ((double) rank / total) * 100;

        if (percentile <= rateInfo.rateS()) return "S";
        if (percentile <= rateInfo.rateS() + rateInfo.rateA()) return "A";
        if (percentile <= rateInfo.rateS() + rateInfo.rateA() + rateInfo.rateB()) return "B";
        if (percentile <= rateInfo.rateS() + rateInfo.rateA() + rateInfo.rateB() + rateInfo.rateC()) return "C";
        return "D";
    }


    // 이의 제기 상세 내역을 조회하는 메서드
    @Override
    public ObjectionDetailResultDto getObjectionDetail(Long objectionId) {
        log.info("[MyObjectionQueryServiceImpl] getObjectionDetail() 호출 - objectionId={}", objectionId);

        // 1) 기본 상세 정보 조회
        ObjectionItemDto base = mapper.findObjectionDetail(objectionId);
        if (base == null) {
            log.error("이의제기 상세 정보를 찾을 수 없음 - objectionId={}", objectionId);
            throw new HrException(ErrorCode.MY_OBJECTIONS_NOT_FOUND);
        }
        log.info("이의제기 기본 정보 조회 완료 - objectionId={}", objectionId);

        Long resultId = base.resultId();
        log.info("결과 ID 조회 - resultId={}", resultId);

        // 2) 요인별 점수 조회
        List<FactorScoreDto> scores = mapper.findFactorScores(resultId);
        if (scores == null || scores.isEmpty()) {
            log.error("요인별 점수를 찾을 수 없음 - resultId={}", resultId);
            throw new HrException(ErrorCode.FACTOR_SCORES_NOT_FOUND);
        }
        log.info("요인별 점수 조회 완료 - resultId={}", resultId);

        // 3) 가중치 정보 조회
        WeightInfo weightInfo = mapper.findWeightInfo(resultId);
        if (weightInfo == null) {
            log.error("가중치 정보를 찾을 수 없음 - resultId={}", resultId);
            throw new HrException(ErrorCode.WEIGHT_INFO_NOT_FOUND);
        }
        log.info("가중치 정보 조회 완료 - resultId={}", resultId);

        // 4) 등급 비율 정보 조회
        RateInfo rateInfo = mapper.findRateInfo(resultId);
        if (rateInfo == null) {
            log.error("등급 비율 정보를 찾을 수 없음 - resultId={}", resultId);
            throw new HrException(ErrorCode.RATE_INFO_NOT_FOUND);
        }
        log.info("등급 비율 정보 조회 완료 - resultId={}", resultId);

        // 5) 상세 내역 조립 후 반환
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