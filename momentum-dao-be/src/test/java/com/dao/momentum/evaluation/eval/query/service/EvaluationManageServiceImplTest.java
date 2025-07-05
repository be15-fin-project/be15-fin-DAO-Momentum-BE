package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvaluationRoundStatus;
import com.dao.momentum.evaluation.eval.query.dto.request.*;
import com.dao.momentum.evaluation.eval.query.dto.response.*;
import com.dao.momentum.evaluation.eval.query.mapper.EvaluationManageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("다면 평가 회차 목록 조회")
    class GetEvaluationRounds {

        @Test
        @DisplayName("성공 - 상태 필터 포함")
        void getEvaluationRounds_success_withStatusFilter() {
            // given
            EvaluationRoundListRequestDto requestDto = EvaluationRoundListRequestDto.builder()
                    .startDate(LocalDate.of(2025, 7, 1))
                    .endDate(LocalDate.of(2025, 7, 31))
                    .status(EvaluationRoundStatus.IN_PROGRESS)
                    .page(1)
                    .size(10)
                    .build();

            EvaluationRoundResponseDto dto = EvaluationRoundResponseDto.builder()
                    .roundId(1)
                    .roundNo(2)
                    .startAt(LocalDate.now().minusDays(1))
                    .endAt(LocalDate.now().plusDays(6))
                    .participantCount(15)
                    .status(null)
                    .build();

            given(evaluationManageMapper.countEvaluationRounds(requestDto)).willReturn(1L);
            given(evaluationManageMapper.findEvaluationRounds(requestDto)).willReturn(List.of(dto));

            // when
            EvaluationRoundListResultDto result = evaluationManageService.getEvaluationRounds(requestDto);

            // then
            assertThat(result.list()).hasSize(1);
            assertThat(result.list().get(0).status()).isEqualTo(EvaluationRoundStatus.IN_PROGRESS);

            Pagination pagination = result.pagination();
            assertThat(pagination.getCurrentPage()).isEqualTo(1);
            assertThat(pagination.getTotalItems()).isEqualTo(1L);

            verify(evaluationManageMapper).countEvaluationRounds(requestDto);
            verify(evaluationManageMapper).findEvaluationRounds(requestDto);
        }

        @Test
        @DisplayName("필터 조건 불일치로 빈 결과 반환")
        void getEvaluationRounds_filteredOut() {
            // given
            EvaluationRoundListRequestDto requestDto = EvaluationRoundListRequestDto.builder()
                    .startDate(LocalDate.of(2025, 7, 1))
                    .endDate(LocalDate.of(2025, 7, 31))
                    .status(EvaluationRoundStatus.DONE)
                    .page(1)
                    .size(10)
                    .build();

            EvaluationRoundResponseDto dto = EvaluationRoundResponseDto.builder()
                    .roundId(1)
                    .roundNo(2)
                    .startAt(LocalDate.now().plusDays(1))
                    .endAt(LocalDate.now().plusDays(8))
                    .participantCount(10)
                    .status(null)
                    .build();

            given(evaluationManageMapper.countEvaluationRounds(requestDto)).willReturn(1L);
            given(evaluationManageMapper.findEvaluationRounds(requestDto)).willReturn(List.of(dto));

            // when
            EvaluationRoundListResultDto result = evaluationManageService.getEvaluationRounds(requestDto);

            // then
            assertThat(result.list()).isEmpty();
            assertThat(result.pagination().getTotalItems()).isEqualTo(1L);
        }

        @Test
        @DisplayName("총 개수 0일 경우 빈 목록 반환")
        void getEvaluationRounds_emptyFromStart() {
            // given
            EvaluationRoundListRequestDto requestDto = EvaluationRoundListRequestDto.builder()
                    .page(1)
                    .size(10)
                    .build();

            given(evaluationManageMapper.countEvaluationRounds(requestDto)).willReturn(0L);

            // when
            EvaluationRoundListResultDto result = evaluationManageService.getEvaluationRounds(requestDto);

            // then
            assertThat(result.list()).isEmpty();
            assertThat(result.pagination().getTotalItems()).isEqualTo(0L);

            verify(evaluationManageMapper).countEvaluationRounds(requestDto);
            verify(evaluationManageMapper, never()).findEvaluationRounds(any());
        }
    }

    @Nested
    @DisplayName("평가 양식 목록 조회")
    class GetEvaluationForms {

        @Test
        @DisplayName("성공 - typeId 필터 포함")
        void getEvaluationForms_success_withTypeId() {
            // given
            EvaluationFormListRequestDto request = EvaluationFormListRequestDto.builder()
                    .typeId(2)
                    .build();

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
            assertThat(result.get(0).typeId()).isEqualTo(2);
            assertThat(result.get(0).name()).isEqualTo("ORG_COMMITMENT");

            verify(evaluationManageMapper).findEvaluationForms(request);
        }

        @Test
        @DisplayName("결과 없음")
        void getEvaluationForms_empty() {
            // given
            EvaluationFormListRequestDto request = EvaluationFormListRequestDto.builder()
                    .typeId(99)
                    .build();

            given(evaluationManageMapper.findEvaluationForms(request))
                    .willReturn(List.of());

            // when
            List<EvaluationFormResponseDto> result = evaluationManageService.getEvaluationForms(request);

            // then
            assertThat(result).isEmpty();
            verify(evaluationManageMapper).findEvaluationForms(request);
        }
    }

    @Nested
    @DisplayName("평가 회차 번호 및 ID 목록 조회")
    class GetSimpleRoundList {

        @Test
        @DisplayName("성공")
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
            assertThat(result.get(0).roundId()).isEqualTo(1L);
            assertThat(result.get(0).roundNo()).isEqualTo("2025-1차");

            verify(evaluationManageMapper).findSimpleRounds();
        }
    }

    @Nested
    @DisplayName("평가 종류 트리 조회")
    class GetFormTree {

        @Test
        @DisplayName("성공")
        void getFormTree_success() {
            // given
            EvaluationTypeDto type1 = new EvaluationTypeDto(1L, "PEER", "사원 간 평가");
            EvaluationTypeDto type2 = new EvaluationTypeDto(2L, "ORG", "조직 평가");

            EvaluationFormDto form1 = new EvaluationFormDto(10L, "동료 평가", "같은 부서 동료 대상 평가", 1L);
            EvaluationFormDto form2 = new EvaluationFormDto(20L, "조직 몰입도 평가", "조직 몰입 척도", 2L);

            given(evaluationManageMapper.findAllEvalTypes()).willReturn(List.of(type1, type2));
            given(evaluationManageMapper.findAllActiveForms()).willReturn(List.of(form1, form2));

            // when
            List<EvaluationTypeTreeResponseDto> result = evaluationManageService.getFormTree();

            // then
            assertThat(result).hasSize(2);

            EvaluationTypeTreeResponseDto peerTree = result.stream()
                    .filter(tree -> tree.typeId() == 1L)
                    .findFirst()
                    .orElseThrow();

            assertThat(peerTree.typeName()).isEqualTo("PEER");
            assertThat(peerTree.children()).hasSize(1);
            assertThat(peerTree.children().get(0).formId()).isEqualTo(10L);

            EvaluationTypeTreeResponseDto orgTree = result.stream()
                    .filter(tree -> tree.typeId() == 2L)
                    .findFirst()
                    .orElseThrow();

            assertThat(orgTree.typeName()).isEqualTo("ORG");
            assertThat(orgTree.children()).hasSize(1);
            assertThat(orgTree.children().get(0).formId()).isEqualTo(20L);

            verify(evaluationManageMapper).findAllEvalTypes();
            verify(evaluationManageMapper).findAllActiveForms();
        }
    }

    @Nested
    @DisplayName("평가 양식별 요인 조회")
    class GetFormProperties {

        @Test
        @DisplayName("성공")
        void getFormProperties_success() {
            // given
            EvaluationFormPropertyRequestDto request = EvaluationFormPropertyRequestDto.builder()
                    .formId(5L)
                    .build();

            EvaluationFormPropertyDto propDto = EvaluationFormPropertyDto.builder()
                    .propertyId(101L)
                    .name("몰입도")
                    .build();

            given(evaluationManageMapper.findFormProperties(5L)).willReturn(List.of(propDto));

            // when
            List<EvaluationFormPropertyDto> result = evaluationManageService.getFormProperties(request);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).propertyId()).isEqualTo(101L);
            assertThat(result.get(0).name()).isEqualTo("몰입도");

            verify(evaluationManageMapper).findFormProperties(5L);
        }
    }

    @Nested
    @DisplayName("평가 진행 여부 조회")
    class GetTodayRoundStatus {

        @Test
        @DisplayName("진행 중인 회차 존재")
        void getTodayRoundStatus_inProgress() {
            // given
            LocalDate today = LocalDate.now();
            List<Long> roundIds = List.of(100L); // 진행 중 회차 하나 존재

            given(evaluationManageMapper.findOngoingRoundIds(today))
                    .willReturn(roundIds);

            // when
            EvaluationRoundStatusDto result = evaluationManageService.getTodayRoundStatus();

            // then
            assertThat(result.inProgress()).isTrue();
            assertThat(result.roundId()).isEqualTo(100L);

            verify(evaluationManageMapper).findOngoingRoundIds(today);
        }

        @Test
        @DisplayName("진행 중인 회차 없음")
        void getTodayRoundStatus_notInProgress() {
            // given
            LocalDate today = LocalDate.now();

            given(evaluationManageMapper.findOngoingRoundIds(today))
                    .willReturn(List.of());

            // when
            EvaluationRoundStatusDto result = evaluationManageService.getTodayRoundStatus();

            // then
            assertThat(result.inProgress()).isFalse();
            assertThat(result.roundId()).isNull();

            verify(evaluationManageMapper).findOngoingRoundIds(today);
        }
    }

}
