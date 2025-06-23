package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.mapper.EvaluationHrMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class EvaluationHrServiceImplTest {

    @Mock
    private EvaluationHrMapper mapper;

    @InjectMocks
    private EvaluationHrServiceImpl service;

    private MyHrEvaluationListRequestDto req;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        req = new MyHrEvaluationListRequestDto();
        req.setPage(1);
        req.setSize(10);
    }

    @Test
    @DisplayName("getHrEvaluations: 평가 목록을 정상 반환")
    void getHrEvaluations_success() {
        HrEvaluationItemDto item = HrEvaluationItemDto.builder()
                .resultId(1001L)
                .roundNo(3)
                .overallGrade("A")
                .evaluatedAt(LocalDateTime.now())
                .build();

        given(mapper.findHrEvaluations(eq(1L), any(MyHrEvaluationListRequestDto.class))).willReturn(List.of(item));
        given(mapper.countHrEvaluations(eq(1L), any(MyHrEvaluationListRequestDto.class))).willReturn(1L);
        given(mapper.findFactorScores(1001L)).willReturn(List.of()); // 검증 대상 아님

        HrEvaluationListResultDto result = service.getHrEvaluations(1L, req);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getResultId()).isEqualTo(1001L);
        assertThat(result.getPagination().getTotalItems()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getHrEvaluationDetail: 상세조회 정상")
    void getHrEvaluationDetail_success() {
        Long resultId = 1001L;
        Long empId = 1L;

        HrEvaluationDetailDto content = HrEvaluationDetailDto.builder()
                .resultId(resultId)
                .empNo("20250001")
                .empName("김현우")
                .overallGrade("S")
                .evaluatedAt(LocalDateTime.now())
                .build();

        RateInfo rate = RateInfo.builder().rateS(10).rateA(20).rateB(30).rateC(25).rateD(15).build();
        WeightInfo weight = WeightInfo.builder()
                .weightPerform(25).weightTeam(20).weightAttitude(15)
                .weightGrowth(10).weightEngagement(15).weightResult(15).build();
        FactorScoreDto score = FactorScoreDto.builder().propertyName("태도").score("B").build();

        given(mapper.findEvaluationContent(resultId, empId)).willReturn(content);
        given(mapper.findRateInfo(resultId)).willReturn(rate);
        given(mapper.findWeightInfo(resultId)).willReturn(weight);
        given(mapper.findFactorScores(resultId)).willReturn(List.of(score));

        HrEvaluationDetailResultDto result = service.getHrEvaluationDetail(empId, resultId);

        assertThat(result.getContent().getResultId()).isEqualTo(resultId);
        assertThat(result.getFactorScores()).hasSize(1);
        assertThat(result.getRateInfo().getRateS()).isEqualTo(10);
        assertThat(result.getWeightInfo().getWeightPerform()).isEqualTo(25);
    }

    @Test
    @DisplayName("getHrEvaluationDetail: 조회 결과 없으면 예외 발생")
    void getHrEvaluationDetail_notFound_throws() {
        given(mapper.findEvaluationContent(1001L, 1L)).willReturn(null);

        assertThatThrownBy(() -> service.getHrEvaluationDetail(1L, 1001L))
                .isInstanceOf(HrException.class)
                .satisfies(e -> {
                    HrException ex = (HrException) e;
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.HR_EVALUATION_NOT_FOUND);
                });

    }

    @Test
    @DisplayName("getEvaluationCriteria: 회차 없는 경우 최신 회차로 조회")
    void getEvaluationCriteria_nullRoundNo_returnsLatest() {
        given(mapper.findLatestRoundNo()).willReturn(5);
        given(mapper.findRateInfoByRoundNo(5)).willReturn(
                RateInfo.builder().rateS(10).rateA(25).rateB(30).rateC(20).rateD(15).build()
        );
        given(mapper.findWeightInfoByRoundNo(5)).willReturn(
                WeightInfo.builder().weightPerform(20).weightTeam(20).weightAttitude(20)
                        .weightGrowth(20).weightEngagement(10).weightResult(10).build()
        );

        HrEvaluationCriteriaDto result = service.getEvaluationCriteria(null);

        assertThat(result.getRateInfo().getRateA()).isEqualTo(25);
        assertThat(result.getWeightInfo().getWeightResult()).isEqualTo(10);
    }
}
