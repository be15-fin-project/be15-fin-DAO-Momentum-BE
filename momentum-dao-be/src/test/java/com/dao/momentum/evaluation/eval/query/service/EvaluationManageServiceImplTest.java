package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvaluationRoundStatus;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationFormListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.request.EvaluationRoundListRequestDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationFormResponseDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundListResultDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundResponseDto;
import com.dao.momentum.evaluation.eval.query.dto.response.EvaluationRoundSimpleDto;
import com.dao.momentum.evaluation.eval.query.mapper.EvaluationManageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class EvaluationManageServiceImplTest {

    private EvaluationManageServiceImpl evaluationManageService;

    private EvaluationManageMapper evaluationManageMapper = Mockito.mock(EvaluationManageMapper.class);

    @BeforeEach
    void setUp() {
        evaluationManageService = new EvaluationManageServiceImpl(evaluationManageMapper);
    }

    @Test
    @DisplayName("다면 평가 회차 목록 조회 - 성공 (상태 필터 포함)")
    void getEvaluationRounds_success_withStatusFilter() {
        // given
        EvaluationRoundListRequestDto requestDto = new EvaluationRoundListRequestDto();
        ReflectionTestUtils.setField(requestDto, "startDate", LocalDate.of(2025, 7, 1));
        ReflectionTestUtils.setField(requestDto, "endDate", LocalDate.of(2025, 7, 31));
        ReflectionTestUtils.setField(requestDto, "status", EvaluationRoundStatus.IN_PROGRESS);
        ReflectionTestUtils.setField(requestDto, "page", 1);
        ReflectionTestUtils.setField(requestDto, "size", 10);

        EvaluationRoundResponseDto dto = EvaluationRoundResponseDto.builder()
                .roundId(1)
                .roundNo(2)
                .startAt(LocalDate.now().minusDays(1)) // 상태: IN_PROGRESS
                .endAt(LocalDate.now().plusDays(6))
                .participantCount(15)
                .status(null) // DB에서는 null, Java에서 계산
                .build();

        given(evaluationManageMapper.countEvaluationRounds(requestDto)).willReturn(1L);
        given(evaluationManageMapper.findEvaluationRounds(requestDto)).willReturn(List.of(dto));

        // when
        EvaluationRoundListResultDto result = evaluationManageService.getEvaluationRounds(requestDto);

        // then
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getStatus()).isEqualTo(EvaluationRoundStatus.IN_PROGRESS);

        Pagination pagination = result.getPagination();
        assertThat(pagination.getCurrentPage()).isEqualTo(1);
        assertThat(pagination.getTotalItems()).isEqualTo(1L);

        verify(evaluationManageMapper).countEvaluationRounds(requestDto);
        verify(evaluationManageMapper).findEvaluationRounds(requestDto);
    }

    @Test
    @DisplayName("다면 평가 회차 목록 조회 - 필터 조건 불일치로 빈 결과 반환")
    void getEvaluationRounds_filteredOut() {
        // given
        EvaluationRoundListRequestDto requestDto = new EvaluationRoundListRequestDto();
        ReflectionTestUtils.setField(requestDto, "status", EvaluationRoundStatus.DONE);
        ReflectionTestUtils.setField(requestDto, "page", 1);
        ReflectionTestUtils.setField(requestDto, "size", 10);

        EvaluationRoundResponseDto dto = EvaluationRoundResponseDto.builder()
                .roundId(1)
                .roundNo(2)
                .startAt(LocalDate.now().plusDays(1)) // 상태: BEFORE
                .endAt(LocalDate.now().plusDays(8))
                .participantCount(10)
                .status(null)
                .build();

        given(evaluationManageMapper.countEvaluationRounds(requestDto)).willReturn(1L);
        given(evaluationManageMapper.findEvaluationRounds(requestDto)).willReturn(List.of(dto));

        // when
        EvaluationRoundListResultDto result = evaluationManageService.getEvaluationRounds(requestDto);

        // then
        assertThat(result.getList()).isEmpty();
        assertThat(result.getPagination().getTotalItems()).isEqualTo(1L);
    }

    @Test
    @DisplayName("다면 평가 회차 목록 조회 - 총 개수 0일 경우 빈 목록 반환")
    void getEvaluationRounds_emptyFromStart() {
        // given
        EvaluationRoundListRequestDto requestDto = new EvaluationRoundListRequestDto();
        ReflectionTestUtils.setField(requestDto, "page", 1);
        ReflectionTestUtils.setField(requestDto, "size", 10);

        given(evaluationManageMapper.countEvaluationRounds(requestDto)).willReturn(0L);

        // when
        EvaluationRoundListResultDto result = evaluationManageService.getEvaluationRounds(requestDto);

        // then
        assertThat(result.getList()).isEmpty();
        assertThat(result.getPagination().getTotalItems()).isEqualTo(0L);

        verify(evaluationManageMapper).countEvaluationRounds(requestDto);
        verify(evaluationManageMapper, never()).findEvaluationRounds(any());
    }


    @Test
    @DisplayName("평가 양식 목록 조회 - 성공 (typeId 필터 포함)")
    void getEvaluationForms_success_withTypeId() {
        // given
        EvaluationFormListRequestDto request = new EvaluationFormListRequestDto();
        ReflectionTestUtils.setField(request, "typeId", 2); // ORG

        EvaluationFormResponseDto dto = EvaluationFormResponseDto.builder()
                .formId(5)
                .typeId(2)
                .typeName("ORG")
                .name("ORG_COMMITMENT")
                .description("조직 몰입 척도")
                .build();

        given(evaluationManageMapper.findEvaluationForms(request))
                .willReturn(List.of(dto));

        // when
        List<EvaluationFormResponseDto> result = evaluationManageService.getEvaluationForms(request);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTypeId()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("ORG_COMMITMENT");

        verify(evaluationManageMapper).findEvaluationForms(request);
    }

    @Test
    @DisplayName("평가 양식 목록 조회 - 결과 없음")
    void getEvaluationForms_empty() {
        // given
        EvaluationFormListRequestDto request = new EvaluationFormListRequestDto();
        ReflectionTestUtils.setField(request, "typeId", 99); // 존재하지 않는 typeId

        given(evaluationManageMapper.findEvaluationForms(request))
                .willReturn(List.of());

        // when
        List<EvaluationFormResponseDto> result = evaluationManageService.getEvaluationForms(request);

        // then
        assertThat(result).isEmpty();
        verify(evaluationManageMapper).findEvaluationForms(request);
    }

    @Test
    @DisplayName("평가 회차 번호 및 ID 목록 조회 - 성공")
    void getSimpleRoundList_success() {
        // given
        EvaluationRoundSimpleDto dto = EvaluationRoundSimpleDto.builder()
                .roundId(1L)
                .roundNo("2025-1차")
                .build();

        given(evaluationManageMapper.findSimpleRounds()).willReturn(List.of(dto));

        // when
        List<EvaluationRoundSimpleDto> result = evaluationManageService.getSimpleRoundList();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoundId()).isEqualTo(1L);
        assertThat(result.get(0).getRoundNo()).isEqualTo("2025-1차");

        verify(evaluationManageMapper).findSimpleRounds();
    }

}
