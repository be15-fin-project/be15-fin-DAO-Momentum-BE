package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.mapper.EvaluationHrMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationHrServiceImplTest {

    @Mock
    private EvaluationHrMapper mapper;

    @InjectMocks
    private EvaluationHrServiceImpl service;

    private MyHrEvaluationListRequestDto req;

    @BeforeEach
    void setUp() {
        req = new MyHrEvaluationListRequestDto(null, null, null, 1, 10);
    }

    @Nested
    @DisplayName("getHrEvaluations: 평가 목록 조회")
    class GetHrEvaluations {

        @Test
        @DisplayName("정상 반환")
        void getHrEvaluations_success() {
            HrEvaluationItemDto item = HrEvaluationItemDto.builder()
                    .resultId(2001L)
                    .roundNo(3)
                    .overallGrade("A")
                    .evaluatedAt(LocalDateTime.now())
                    .build();

            given(mapper.findHrEvaluations(1L, req)).willReturn(List.of(item));
            given(mapper.countHrEvaluations(1L, req)).willReturn(1L);
            lenient().when(mapper.findFactorScores(2001L)).thenReturn(List.of());

            HrEvaluationListResultDto result = service.getHrEvaluations(1L, req);

            assertThat(result.items()).hasSize(1);
            assertThat(result.items().get(0).item().resultId()).isEqualTo(2001L);
        }

        @Test
        @DisplayName("조회 실패시 예외 발생")
        void getHrEvaluations_notFound_throws() {
            given(mapper.findHrEvaluations(1L, req)).willReturn(null);

            assertThatThrownBy(() -> service.getHrEvaluations(1L, req))
                    .isInstanceOf(HrException.class)
                    .extracting(e -> ((HrException) e).getErrorCode())
                    .isEqualTo(ErrorCode.HR_EVALUATIONS_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getHrEvaluationDetail: 상세조회")
    class GetHrEvaluationDetail {

        @Test
        @DisplayName("정상 조회")
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

            assertThat(result.content().resultId()).isEqualTo(resultId);
            assertThat(result.factorScores()).hasSize(1);
            assertThat(result.rateInfo().rateS()).isEqualTo(10);
            assertThat(result.weightInfo().weightPerform()).isEqualTo(25);
        }

        @Test
        @DisplayName("조회 결과 없으면 예외 발생")
        void getHrEvaluationDetail_notFound_throws() {
            given(mapper.findEvaluationContent(1001L, 1L)).willReturn(null);

            assertThatThrownBy(() -> service.getHrEvaluationDetail(1L, 1001L))
                    .isInstanceOf(HrException.class)
                    .satisfies(e -> assertThat(((HrException) e).getErrorCode()).isEqualTo(ErrorCode.HR_EVALUATION_NOT_FOUND));
        }
    }

    @Nested
    @DisplayName("getEvaluationCriteria: 회차 기준 조회")
    class GetEvaluationCriteria {

        @Test
        @DisplayName("회차 없으면 최신 회차로 조회")
        void getEvaluationCriteria_nullRoundNo_returnsLatest() {
            given(mapper.findLatestRoundNo()).willReturn(5);
            given(mapper.findRateInfoByRoundNo(5)).willReturn(
                    RateInfo.builder().rateS(10).rateA(25).rateB(30).rateC(20).rateD(15).build()
            );
            given(mapper.findWeightInfoByRoundNo(5)).willReturn(
                    WeightInfo.builder()
                            .weightPerform(20).weightTeam(20).weightAttitude(20)
                            .weightGrowth(20).weightEngagement(10).weightResult(10).build()
            );

            HrEvaluationCriteriaDto result = service.getEvaluationCriteria(null);

            assertThat(result.rateInfo().rateA()).isEqualTo(25);
            assertThat(result.weightInfo().weightResult()).isEqualTo(10);
        }
    }
}
