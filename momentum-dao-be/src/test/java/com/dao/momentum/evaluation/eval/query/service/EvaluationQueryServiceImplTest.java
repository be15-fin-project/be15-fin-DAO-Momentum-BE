package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.eval.exception.EvalException;
import com.dao.momentum.evaluation.eval.query.dto.request.OrgEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.PeerEvaluationListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import com.dao.momentum.evaluation.eval.query.mapper.OrgEvaluationMapper;
import com.dao.momentum.evaluation.eval.query.mapper.PeerEvaluationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class EvaluationQueryServiceImplTest {

    private EvaluationQueryServiceImpl evaluationQueryService;

    private PeerEvaluationMapper peerEvaluationMapper = Mockito.mock(PeerEvaluationMapper.class);
    private OrgEvaluationMapper orgEvaluationMapper = Mockito.mock(OrgEvaluationMapper.class);

    @BeforeEach
    void setUp() {
        evaluationQueryService = new EvaluationQueryServiceImpl(peerEvaluationMapper, orgEvaluationMapper);
    }

    @Test
    @DisplayName("사원 간 평가 결과 조회 - 성공")
    void getPeerEvaluations_success() {
        // given
        PeerEvaluationListRequestDto requestDto = new PeerEvaluationListRequestDto();
        ReflectionTestUtils.setField(requestDto, "page", 1);
        ReflectionTestUtils.setField(requestDto, "size", 10);

        PeerEvaluationResponseDto responseDto = PeerEvaluationResponseDto.builder()
                .resultId(1L)
                .evalId(20250001L)
                .evalName("김현우")
                .targetId(20250002L)
                .targetName("정예준")
                .formName("동료 평가")
                .roundNo(2)
                .score(85)
                .reason("협업이 뛰어남")
                .createdAt(LocalDateTime.now())
                .build();

        when(peerEvaluationMapper.countPeerEvaluations(requestDto)).thenReturn(1L);
        when(peerEvaluationMapper.findPeerEvaluations(requestDto)).thenReturn(List.of(responseDto));

        // when
        PeerEvaluationListResultDto result = evaluationQueryService.getPeerEvaluations(requestDto);

        // then
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getPagination().getCurrentPage()).isEqualTo(1);
        assertThat(result.getPagination().getTotalItems()).isEqualTo(1);
        assertThat(result.getList().get(0).getEvalName()).isEqualTo("김현우");

        verify(peerEvaluationMapper).countPeerEvaluations(requestDto);
        verify(peerEvaluationMapper).findPeerEvaluations(requestDto);
    }

    @Test
    @DisplayName("사원 간 평가 결과 조회 - 예외 발생")
    void getPeerEvaluations_nullList_throwsException() {
        // given
        PeerEvaluationListRequestDto requestDto = new PeerEvaluationListRequestDto();
        ReflectionTestUtils.setField(requestDto, "page", 1);
        ReflectionTestUtils.setField(requestDto, "size", 10);

        when(peerEvaluationMapper.countPeerEvaluations(any())).thenReturn(0L);
        when(peerEvaluationMapper.findPeerEvaluations(any())).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> evaluationQueryService.getPeerEvaluations(requestDto))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVALUATION_RESULT_NOT_FOUND.getMessage());

        verify(peerEvaluationMapper).countPeerEvaluations(any());
        verify(peerEvaluationMapper).findPeerEvaluations(any());
    }


    @Test
    @DisplayName("사원 간 평가 상세 조회 - 성공")
    void getPeerEvaluationDetail_success() {
        // given
        Long resultId = 100L;

        PeerEvaluationDetailResponseDto detail = PeerEvaluationDetailResponseDto.builder()
                .resultId(resultId)
                .evalId(20250001L)
                .evalName("김현우")
                .targetId(20250002L)
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

        when(peerEvaluationMapper.findPeerEvaluationDetail(resultId)).thenReturn(detail);
        when(peerEvaluationMapper.findFactorScores(resultId)).thenReturn(factorScores);

        // when
        PeerEvaluationDetailResultDto result = evaluationQueryService.getPeerEvaluationDetail(resultId);

        // then
        assertThat(result.getDetail().getEvalName()).isEqualTo("김현우");
        assertThat(result.getFactorScores()).hasSize(2);
        assertThat(result.getFactorScores().get(0).getPropertyName()).isEqualTo("커뮤니케이션");

        verify(peerEvaluationMapper).findPeerEvaluationDetail(resultId);
        verify(peerEvaluationMapper).findFactorScores(resultId);
    }

    @Test
    @DisplayName("사원 간 평가 상세 조회 - 에러 발생")
    void getPeerEvaluationDetail_notFound_throwsException() {
        // given
        Long resultId = 999L;

        when(peerEvaluationMapper.findPeerEvaluationDetail(resultId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> evaluationQueryService.getPeerEvaluationDetail(resultId))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVALUATION_RESULT_NOT_FOUND.getMessage());

        verify(peerEvaluationMapper).findPeerEvaluationDetail(resultId);
        verify(peerEvaluationMapper, never()).findFactorScores(any());
    }

    @Test
    @DisplayName("조직 평가 목록 조회 - 성공")
    void getOrgEvaluations_success() {
        // given
        OrgEvaluationListRequestDto requestDto = new OrgEvaluationListRequestDto();
        ReflectionTestUtils.setField(requestDto, "page", 1);
        ReflectionTestUtils.setField(requestDto, "size", 10);

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
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getFormName()).isEqualTo("조직 문화 진단");

        Pagination pagination = result.getPagination();
        assertThat(pagination.getCurrentPage()).isEqualTo(1);
        assertThat(pagination.getTotalItems()).isEqualTo(1L);
        assertThat(pagination.getTotalPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("조직 평가 목록 조회 - 결과 없음 예외")
    void getOrgEvaluations_empty() {
        // given
        OrgEvaluationListRequestDto requestDto = new OrgEvaluationListRequestDto();
        ReflectionTestUtils.setField(requestDto, "page", 1);
        ReflectionTestUtils.setField(requestDto, "size", 10);

        given(orgEvaluationMapper.countOrgEvaluations(any())).willReturn(0L);
        given(orgEvaluationMapper.findOrgEvaluations(any())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> evaluationQueryService.getOrgEvaluations(requestDto))
                .isInstanceOf(EvalException.class)
                .hasMessageContaining(ErrorCode.EVALUATION_RESULT_NOT_FOUND.getMessage());
    }
}
