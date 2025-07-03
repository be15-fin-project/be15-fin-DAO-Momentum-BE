package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateDto;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionDeleteResponse;
import com.dao.momentum.evaluation.hr.command.domain.aggregate.HrObjection;
import com.dao.momentum.evaluation.hr.command.domain.repository.HrObjectionRepository;
import com.dao.momentum.evaluation.hr.exception.HrException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class HrObjectionServiceImplTest {

    private HrObjectionRepository objectionRepository;
    private HrObjectionServiceImpl service;

    @BeforeEach
    void setUp() {
        objectionRepository = mock(HrObjectionRepository.class);
        service = new HrObjectionServiceImpl(objectionRepository);
    }

    @Test
    @DisplayName("이의제기 생성 - 성공")
    void create_success() {
        // given
        HrObjectionCreateDto dto = HrObjectionCreateDto.builder()
                .resultId(1L)
                .reason("정당한 사유입니다.")
                .build();
        Long empId = 1001L;

        given(objectionRepository.existsByResultId(dto.getResultId())).willReturn(false);
        given(objectionRepository.existsEvaluation(dto.getResultId())).willReturn(true);

        HrObjection saved = HrObjection.builder()
                .objectionId(10L)
                .createdAt(LocalDateTime.now())
                .build();

        given(objectionRepository.save(any(HrObjection.class))).willReturn(saved);

        // when & then
        assertThatCode(() -> service.create(dto, empId)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이의제기 생성 - 이미 제출된 경우 예외")
    void create_duplicate() {
        HrObjectionCreateDto dto = HrObjectionCreateDto.builder()
                .resultId(1L)
                .reason("중복 제출 테스트")
                .build();
        Long empId = 1001L;

        given(objectionRepository.existsByResultId(dto.getResultId())).willReturn(true);

        assertThatThrownBy(() -> service.create(dto, empId))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.ALREADY_SUBMITTED_OBJECTION.getMessage());
    }

    @Test
    @DisplayName("이의제기 생성 - 평가 결과 없음 → 예외")
    void create_evaluationNotFound() {
        HrObjectionCreateDto dto = HrObjectionCreateDto.builder()
                .resultId(1L)
                .reason("결과 없음 테스트")
                .build();
        Long empId = 1001L;

        given(objectionRepository.existsByResultId(dto.getResultId())).willReturn(false);
        given(objectionRepository.existsEvaluation(dto.getResultId())).willReturn(false);

        assertThatThrownBy(() -> service.create(dto, empId))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.EVALUATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("이의제기 삭제 - 성공")
    void delete_success() {
        Long objectionId = 1L;
        Long empId = 100L;

        HrObjection objection = HrObjection.builder()
                .objectionId(objectionId)
                .writerId(empId)
                .statusId(1)
                .createdAt(LocalDateTime.now())
                .isDeleted(UseStatus.N)
                .build();

        given(objectionRepository.findById(objectionId)).willReturn(Optional.of(objection));

        HrObjectionDeleteResponse response = service.deleteById(objectionId, empId);

        assertThat(response).isNotNull();
        assertThat(response.getObjectionId()).isEqualTo(objectionId);
        assertThat(response.getMessage()).contains("성공적으로 삭제");
        assertThat(objection.getIsDeleted()).isEqualTo(UseStatus.Y);
    }

    @Test
    @DisplayName("이의제기 삭제 - 작성자 아님 예외")
    void delete_forbidden() {
        HrObjection objection = HrObjection.builder()
                .objectionId(1L)
                .writerId(200L)
                .statusId(1)
                .createdAt(LocalDateTime.now())
                .isDeleted(UseStatus.N)
                .build();

        given(objectionRepository.findById(1L)).willReturn(Optional.of(objection));

        assertThatThrownBy(() -> service.deleteById(1L, 999L))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.HR_OBJECTION_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("이의제기 삭제 - 대기 상태 아님 예외")
    void delete_invalidStatus() {
        HrObjection objection = HrObjection.builder()
                .objectionId(1L)
                .writerId(100L)
                .statusId(2)
                .createdAt(LocalDateTime.now())
                .isDeleted(UseStatus.N)
                .build();

        given(objectionRepository.findById(1L)).willReturn(Optional.of(objection));

        assertThatThrownBy(() -> service.deleteById(1L, 100L))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.HR_OBJECTION_CANNOT_DELETE.getMessage());
    }

    @Test
    @DisplayName("이의제기 삭제 - 존재하지 않음")
    void delete_notFound() {
        given(objectionRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById(999L, 100L))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.HR_OBJECTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("이의제기 승인 - 성공")
    void approve_success() {
        HrObjection objection = HrObjection.builder()
                .objectionId(1L)
                .statusId(1)
                .build();

        given(objectionRepository.findById(1L)).willReturn(Optional.of(objection));

        assertThatCode(() -> service.approve(1L, "수정된 사유")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이의제기 반려 - 성공")
    void reject_success() {
        HrObjection objection = HrObjection.builder()
                .objectionId(1L)
                .statusId(1)
                .build();

        given(objectionRepository.findById(1L)).willReturn(Optional.of(objection));

        assertThatCode(() -> service.reject(1L, "반려 사유")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이의제기 ID로 평가 결과 ID 조회 - 성공")
    void getResultId_success() {
        given(objectionRepository.findResultIdByObjectionId(1L)).willReturn(Optional.of(42L));

        Long resultId = service.getResultIdByObjectionId(1L);
        assertThat(resultId).isEqualTo(42L);
    }

    @Test
    @DisplayName("이의제기 ID로 평가 결과 ID 조회 - 실패")
    void getResultId_notFound() {
        given(objectionRepository.findResultIdByObjectionId(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.getResultIdByObjectionId(1L))
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.HR_OBJECTION_NOT_FOUND.getMessage());
    }
}
