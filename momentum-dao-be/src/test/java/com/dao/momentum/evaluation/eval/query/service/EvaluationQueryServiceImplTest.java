package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.FactorScoreDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.OrgEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.various.SelfEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.various.*;
import com.dao.momentum.evaluation.eval.query.mapper.OrgEvaluationMapper;
import com.dao.momentum.evaluation.eval.query.mapper.PeerEvaluationMapper;
import com.dao.momentum.evaluation.eval.query.mapper.SelfEvaluationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class EvaluationQueryServiceImplTest {

    private EvaluationQueryServiceImpl evaluationQueryService;

    private PeerEvaluationMapper peerEvaluationMapper = Mockito.mock(PeerEvaluationMapper.class);
    private OrgEvaluationMapper orgEvaluationMapper = Mockito.mock(OrgEvaluationMapper.class);
    private SelfEvaluationMapper selfEvaluationMapper = Mockito.mock(SelfEvaluationMapper.class);

    @BeforeEach
    void setUp() {
        evaluationQueryService = new EvaluationQueryServiceImpl(peerEvaluationMapper, orgEvaluationMapper, selfEvaluationMapper);
    }

    @Nested
    @DisplayName("사원 간 평가 조회 (Get Peer Evaluations)")
    class GetPeerEvaluations {

        @Test
        @DisplayName("사원 간 평가 결과 조회 - 성공")
        void getPeerEvaluations_success() {
            // given
            PeerEvaluationListRequestDto requestDto = PeerEvaluationListRequestDto.builder()
                    .evalNo("1001")
                    .deptId(10L)
                    .positionId(5L)
                    .targetNo(20250002L)
                    .formId(3)
                    .roundNo(2)
                    .page(1)    // optional, defaults to 1 if not provided
                    .size(10)   
                    .build();

            PeerEvaluationResponseDto responseDto = PeerEvaluationResponseDto.builder()
                    .resultId(1L)
                    .evalNo("20250001")
                    .evalName("김현우")
                    .targetNo("20250002")
                    .targetName("정예준")
                    .formName("동료 평가")
                    .roundNo(2)
                    .score(85)
                    .reason("협업이 뛰어남")
                    .createdAt(LocalDateTime.now())
                    .build();

            given(peerEvaluationMapper.countPeerEvaluations(requestDto)).willReturn(1L);
            given(peerEvaluationMapper.findPeerEvaluations(requestDto)).willReturn(List.of(responseDto));

            // when
            PeerEvaluationListResultDto result = evaluationQueryService.getPeerEvaluations(requestDto);

            // then
            assertThat(result.list()).hasSize(1);
            assertThat(result.pagination().getCurrentPage()).isEqualTo(1);
            assertThat(result.pagination().getTotalItems()).isEqualTo(1);
            assertThat(result.list().get(0).evalName()).isEqualTo("김현우");

            verify(peerEvaluationMapper).countPeerEvaluations(requestDto);
            verify(peerEvaluationMapper).findPeerEvaluations(requestDto);
        }

        @Test
        @DisplayName("사원 간 평가 결과 조회 - 예외 발생")
        void getPeerEvaluations_nullList_throwsException() {
            // given
            PeerEvaluationListRequestDto requestDto = PeerEvaluationListRequestDto.builder()
                    .evalNo("1001")
                    .deptId(10L)
                    .positionId(5L)
                    .targetNo(20250002L)
                    .formId(3)
                    .roundNo(2)
                    .page(1)    // optional, defaults to 1 if not provided
                    .size(10)   
                    .build();

            given(peerEvaluationMapper.countPeerEvaluations(any())).willReturn(0L);
            given(peerEvaluationMapper.findPeerEvaluations(any())).willReturn(null);

            // when & then
            assertThatThrownBy(() -> evaluationQueryService.getPeerEvaluations(requestDto))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVALUATION_RESULT_NOT_FOUND.getMessage());

            verify(peerEvaluationMapper).countPeerEvaluations(any());
            verify(peerEvaluationMapper).findPeerEvaluations(any());
        }
    }

    @Nested
    @DisplayName("사원 간 평가 상세 조회 (Get Peer Evaluation Detail)")
    class GetPeerEvaluationDetail {

        @Test
        @DisplayName("사원 간 평가 상세 조회 - 성공")
        void getPeerEvaluationDetail_success() {
            // given
            Long resultId = 100L;

            PeerEvaluationResponseDto detail = PeerEvaluationResponseDto.builder()
                    .resultId(resultId)
                    .evalNo("20250001")
                    .evalName("김현우")
                    .targetNo("20250002")
                    .targetName("정예준")
                    .formName("동료 평가")
                    .roundNo(2)
                    .score(90)
                    .reason("정확하고 책임감 있음")
                    .createdAt(LocalDateTime.now())
                    .build();

            FactorScoreDto factorScore1 = FactorScoreDto.builder()
                    .propertyName("커뮤니케이션")
                    .score(92)
                    .build();

            FactorScoreDto factorScore2 = FactorScoreDto.builder()
                    .propertyName("문제해결")
                    .score(88)
                    .build();

            List<FactorScoreDto> factorScores = List.of(factorScore1, factorScore2);

            given(peerEvaluationMapper.findPeerEvaluationDetail(resultId)).willReturn(detail);
            given(peerEvaluationMapper.findFactorScores(resultId)).willReturn(factorScores);

            // when
            PeerEvaluationDetailResultDto result = evaluationQueryService.getPeerEvaluationDetail(resultId);

            // then
            assertThat(result.detail().evalName()).isEqualTo("김현우");
            assertThat(result.factorScores()).hasSize(2);
            assertThat(result.factorScores().get(0).propertyName()).isEqualTo("커뮤니케이션");

            verify(peerEvaluationMapper).findPeerEvaluationDetail(resultId);
            verify(peerEvaluationMapper).findFactorScores(resultId);
        }

        @Test
        @DisplayName("사원 간 평가 상세 조회 - 에러 발생")
        void getPeerEvaluationDetail_notFound_throwsException() {
            // given
            Long resultId = 999L;

            given(peerEvaluationMapper.findPeerEvaluationDetail(resultId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> evaluationQueryService.getPeerEvaluationDetail(resultId))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVALUATION_RESULT_NOT_FOUND.getMessage());

            verify(peerEvaluationMapper).findPeerEvaluationDetail(resultId);
            verify(peerEvaluationMapper, never()).findFactorScores(any());
        }
    }

    @Nested
    @DisplayName("조직 평가 조회 (Get Org Evaluations)")
    class GetOrgEvaluations {

        @Test
        @DisplayName("조직 평가 목록 조회 - 성공")
        void getOrgEvaluations_success() {
            // given
            OrgEvaluationListRequestDto requestDto = OrgEvaluationListRequestDto.builder()
                    .page(1)  // Manually setting page to 1
                    .size(10) // Manually setting size to 10
                    .build();

            OrgEvaluationResponseDto dto = OrgEvaluationResponseDto.builder()
                    .formName("조직 문화 진단")
                    .roundNo(2)
                    .score(87)
                    .build();

            given(orgEvaluationMapper.countOrgEvaluations(any())).willReturn(1L);
            given(orgEvaluationMapper.findOrgEvaluations(any())).willReturn(List.of(dto));

            // when
            OrgEvaluationListResultDto result = evaluationQueryService.getOrgEvaluations(requestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.list()).hasSize(1);
            assertThat(result.list().get(0).formName()).isEqualTo("조직 문화 진단");

            Pagination pagination = result.pagination();
            assertThat(pagination.getCurrentPage()).isEqualTo(1);
            assertThat(pagination.getTotalItems()).isEqualTo(1L);
            assertThat(pagination.getTotalPage()).isEqualTo(1);
        }

        @Test
        @DisplayName("조직 평가 목록 조회 - 결과 없음 예외")
        void getOrgEvaluations_empty() {
            // given
            OrgEvaluationListRequestDto requestDto = OrgEvaluationListRequestDto.builder()
                    .page(1)  // Manually setting page to 1
                    .size(10) // Manually setting size to 10
                    .build();


            // Set up mock to return zero count
            given(orgEvaluationMapper.countOrgEvaluations(any())).willReturn(0L);

            // when
            OrgEvaluationListResultDto result = evaluationQueryService.getOrgEvaluations(requestDto);

            // then
            assertThat(result.list()).isEmpty();  // 결과가 빈 리스트임을 확인
            assertThat(result.pagination()).isNotNull();  // 페이지네이션이 설정되어 있는지 확인

            verify(orgEvaluationMapper).countOrgEvaluations(any());
            verify(orgEvaluationMapper, times(0)).findOrgEvaluations(any());  // findOrgEvaluations는 호출되지 않아야 함
        }

    }

    @Nested
    @DisplayName("조직 평가 상세 조회 (Get Org Evaluation Detail)")
    class GetOrgEvaluationDetail {

        @Test
        @DisplayName("조직 평가 상세 조회 - 성공")
        void getOrgEvaluationDetail_success() {
            // given
            Long resultId = 500L;

            OrgEvaluationResponseDto detail = OrgEvaluationResponseDto.builder()
                    .resultId(resultId)
                    .empNo("20250001")
                    .evalName("김현우")
                    .formName("직무 스트레스 자가진단")
                    .roundNo(2)
                    .score(80)
                    .createdAt(LocalDateTime.of(2025, 6, 20, 15, 0))
                    .build();

            List<FactorScoreDto> factorScores = List.of(
                    FactorScoreDto.builder().propertyName("스트레스 요인").score(85).build(),
                    FactorScoreDto.builder().propertyName("스트레스 반응").score(75).build()
            );

            given(orgEvaluationMapper.findOrgEvaluationDetail(resultId)).willReturn(detail);
            given(orgEvaluationMapper.findOrgFactorScores(resultId)).willReturn(factorScores);

            // when
            OrgEvaluationDetailResultDto result = evaluationQueryService.getOrgEvaluationDetail(resultId);

            // then
            assertThat(result.factorScores()).hasSize(2);
            assertThat(result.detail().formName()).isEqualTo("직무 스트레스 자가진단");
            assertThat(result.factorScores().get(0).propertyName()).isEqualTo("스트레스 요인");

            verify(orgEvaluationMapper).findOrgEvaluationDetail(resultId);
            verify(orgEvaluationMapper).findOrgFactorScores(resultId);
        }

        @Test
        @DisplayName("조직 평가 상세 조회 - 결과 없음 예외")
        void getOrgEvaluationDetail_notFound() {
            // given
            Long resultId = 999L;
            given(orgEvaluationMapper.findOrgEvaluationDetail(resultId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> evaluationQueryService.getOrgEvaluationDetail(resultId))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVALUATION_RESULT_NOT_FOUND.getMessage());

            verify(orgEvaluationMapper).findOrgEvaluationDetail(resultId);
            verify(orgEvaluationMapper, never()).findOrgFactorScores(any());
        }
    }

    @Nested
    @DisplayName("자가 진단 평가 조회 (Get Self Evaluations)")
    class GetSelfEvaluations {

        @Test
        @DisplayName("자가 진단 평가 결과 목록 조회 - 성공")
        void getSelfEvaluations_success() {
            // given
            SelfEvaluationListRequestDto requestDto = SelfEvaluationListRequestDto.builder()
                    .empNo("20250001")
                    .deptId(10L)
                    .positionId(5L)
                    .formId(5L)
                    .roundNo(2L)
                    .page(1)   // optional, defaults to 1 if not provided
                    .size(10)  
                    .build();

            SelfEvaluationResponseDto responseDto = SelfEvaluationResponseDto.builder()
                    .resultId(201L)
                    .empNo("20250001")
                    .evalName("김하진")
                    .formName("직업 만족도 진단")
                    .roundNo(1)
                    .score(82)
                    .reason("업무 만족도는 보통 이상")
                    .createdAt(LocalDateTime.of(2025, 6, 21, 14, 30))
                    .build();

            given(selfEvaluationMapper.countSelfEvaluations(requestDto)).willReturn(1L);
            given(selfEvaluationMapper.findSelfEvaluations(requestDto)).willReturn(List.of(responseDto));

            // when
            SelfEvaluationListResultDto result = evaluationQueryService.getSelfEvaluations(requestDto);

            // then
            assertThat(result.list()).hasSize(1);
            assertThat(result.list().get(0).empNo()).isEqualTo("20250001");
            assertThat(result.pagination().getCurrentPage()).isEqualTo(1);
            assertThat(result.pagination().getTotalItems()).isEqualTo(1L);

            verify(selfEvaluationMapper).countSelfEvaluations(requestDto);
            verify(selfEvaluationMapper).findSelfEvaluations(requestDto);
        }

        @Test
        @DisplayName("자가 진단 평가 결과 목록 조회 - 결과 없음")
        void getSelfEvaluations_empty() {
            // given
            SelfEvaluationListRequestDto requestDto = SelfEvaluationListRequestDto.builder()
                    .empNo("99999999")
                    .deptId(10L)
                    .positionId(5L)
                    .formId(5L)
                    .roundNo(2L)
                    .page(1)   // optional, defaults to 1 if not provided
                    .size(10)  
                    .build();

            // Set up mock to return zero count
            given(selfEvaluationMapper.countSelfEvaluations(any())).willReturn(0L);

            // when
            SelfEvaluationListResultDto result = evaluationQueryService.getSelfEvaluations(requestDto);

            // then
            assertThat(result.list()).isEmpty();  // 결과가 빈 리스트임을 확인
            assertThat(result.pagination()).isNotNull();  // 페이지네이션이 설정되어 있는지 확인

            verify(selfEvaluationMapper).countSelfEvaluations(any());
            verify(selfEvaluationMapper, times(0)).findSelfEvaluations(any());  // findSelfEvaluations는 호출되지 않아야 함
        }

    }

    @Nested
    @DisplayName("자가 진단 평가 상세 조회 (Get Self Evaluation Detail)")
    class GetSelfEvaluationDetail {

        @Test
        @DisplayName("자가 진단 평가 상세 조회 - 성공")
        void getSelfEvaluationDetail_success() {
            // given
            Long resultId = 301L;

            SelfEvaluationResponseDto detail = SelfEvaluationResponseDto.builder()
                    .resultId(resultId)
                    .empNo("20250001")
                    .evalName("김하진")
                    .formName("직무 스트레스 자가진단")
                    .roundNo(1)
                    .score(75)
                    .reason("스트레스 요인 일부 존재")
                    .createdAt(LocalDateTime.of(2025, 6, 22, 9, 30))
                    .build();

            List<FactorScoreDto> factorScores = List.of(
                    FactorScoreDto.builder().propertyName("스트레스 반응").score(78).build(),
                    FactorScoreDto.builder().propertyName("스트레스 요인").score(72).build()
            );

            given(selfEvaluationMapper.findSelfEvaluationDetail(resultId)).willReturn(detail);
            given(selfEvaluationMapper.findFactorScores(resultId)).willReturn(factorScores);

            // when
            SelfEvaluationDetailResultDto result = evaluationQueryService.getSelfEvaluationDetail(resultId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.detail().empNo()).isEqualTo("20250001");
            assertThat(result.detail().evalName()).isEqualTo("김하진");
            assertThat(result.factorScores()).hasSize(2);
            assertThat(result.factorScores().get(1).score()).isEqualTo(72);

            verify(selfEvaluationMapper).findSelfEvaluationDetail(resultId);
            verify(selfEvaluationMapper).findFactorScores(resultId);
        }

        @Test
        @DisplayName("자가 진단 평가 상세 조회 - 결과 없음 예외")
        void getSelfEvaluationDetail_notFound_throwsException() {
            // given
            Long resultId = 9999L;

            given(selfEvaluationMapper.findSelfEvaluationDetail(resultId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> evaluationQueryService.getSelfEvaluationDetail(resultId))
                    .isInstanceOf(EvalException.class)
                    .hasMessageContaining(ErrorCode.EVALUATION_RESULT_NOT_FOUND.getMessage());

            verify(selfEvaluationMapper).findSelfEvaluationDetail(resultId);
            verify(selfEvaluationMapper, never()).findFactorScores(any());
        }
    }
}
