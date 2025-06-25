package com.dao.momentum.retention.command.application.service;

import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.command.application.dto.request.RetentionContactCreateDto;
import com.dao.momentum.retention.command.application.dto.request.RetentionContactDeleteDto;
import com.dao.momentum.retention.command.application.dto.request.RetentionContactFeedbackUpdateDto;
import com.dao.momentum.retention.command.application.dto.request.RetentionContactResponseUpdateDto;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactDeleteResponse;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactFeedbackUpdateResponse;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactResponse;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactResponseUpdateResponse;
import com.dao.momentum.retention.command.domain.aggregate.RetentionContact;
import com.dao.momentum.retention.command.domain.repository.RetentionContactRepository;
import com.dao.momentum.retention.exception.RetentionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class RetentionContactCommandServiceImplTest {

    @Mock
    private RetentionContactRepository repository;

    @InjectMocks
    private RetentionContactCommandServiceImpl service;

    @Test
    @DisplayName("면담 요청 등록 성공")
    void createContact_success() {
        // given
        RetentionContactCreateDto dto = RetentionContactCreateDto.builder()
                .targetId(1001L)
                .managerId(2002L)
                .writerId(3003L)
                .reason("근무 태도 관련 상담 필요")
                .build();

        RetentionContact entity = RetentionContact.builder()
                .retentionId(1L)
                .targetId(dto.targetId())
                .managerId(dto.managerId())
                .writerId(dto.writerId())
                .reason(dto.reason())
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.save(org.mockito.ArgumentMatchers.any(RetentionContact.class)))
                .thenReturn(entity);

        // when
        RetentionContactResponse result = service.createContact(dto);

        // then
        assertThat(result.retentionId()).isEqualTo(1L);
        assertThat(result.targetId()).isEqualTo(1001L);
        assertThat(result.managerId()).isEqualTo(2002L);
        assertThat(result.writerId()).isEqualTo(3003L);
        assertThat(result.reason()).isEqualTo("근무 태도 관련 상담 필요");
    }

    @Test
    @DisplayName("면담 요청 등록 실패 - 대상자와 상급자가 동일")
    void createContact_sameTargetAndManager_throwsException() {
        // given
        RetentionContactCreateDto dto = RetentionContactCreateDto.builder()
                .targetId(1234L)
                .managerId(1234L) // 동일한 ID
                .writerId(9999L)
                .reason("상담 필요")
                .build();

        // when & then
        assertThatThrownBy(() -> service.createContact(dto))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_CONTACT_TARGET_EQUALS_MANAGER.getMessage());
    }

    @Test
    @DisplayName("면담 요청 삭제 성공 - 작성자 본인")
    void deleteContact_success_asWriter() {
        // given
        Long writerId = 3003L;
        Long retentionId = 1L;
        RetentionContact entity = RetentionContact.builder()
                .retentionId(retentionId)
                .targetId(1001L)
                .managerId(2002L)
                .writerId(writerId)
                .reason("테스트")
                .createdAt(LocalDateTime.now())
                .isDeleted(UseStatus.N)
                .build();

        when(repository.findById(retentionId)).thenReturn(Optional.of(entity));

        RetentionContactDeleteDto dto = RetentionContactDeleteDto.builder()
                .retentionId(retentionId)
                .loginEmpId(writerId)
                .build();

        // when
        RetentionContactDeleteResponse result = service.deleteContact(dto);

        // then
        assertThat(result.retentionId()).isEqualTo(retentionId);
        assertThat(result.message()).contains("삭제");
    }

    @Test
    @DisplayName("면담 요청 삭제 실패 - 이미 삭제된 상태")
    void deleteContact_alreadyDeleted_throwsException() {
        // given
        Long retentionId = 1L;
        RetentionContact deletedContact = RetentionContact.builder()
                .retentionId(retentionId)
                .writerId(3003L)
                .isDeleted(UseStatus.Y) // 이미 삭제됨
                .build();

        when(repository.findById(retentionId)).thenReturn(Optional.of(deletedContact));

        RetentionContactDeleteDto dto = RetentionContactDeleteDto.builder()
                .retentionId(retentionId)
                .loginEmpId(3003L)
                .build();

        // when & then
        assertThatThrownBy(() -> service.deleteContact(dto))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_CONTACT_ALREADY_DELETED.getMessage());
    }

    @Test
    @DisplayName("면담 요청 삭제 실패 - 권한 없음")
    void deleteContact_noPermission_throwsException() {
        // given
        Long retentionId = 1L;
        RetentionContact contact = RetentionContact.builder()
                .retentionId(retentionId)
                .writerId(3003L)
                .isDeleted(UseStatus.N)
                .build();

        when(repository.findById(retentionId)).thenReturn(Optional.of(contact));

        RetentionContactDeleteDto dto = RetentionContactDeleteDto.builder()
                .retentionId(retentionId)
                .loginEmpId(9999L) // 작성자 아님
                .build();

        // when & then
        assertThatThrownBy(() -> service.deleteContact(dto))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_CONTACT_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("면담 보고 성공 - manager가 직접 응답 등록")
    void reportResponse_success() {
        // given
        Long retentionId = 1L;
        Long managerId = 2002L;
        String responseContent = "면담을 6월 25일에 진행하였음";

        RetentionContact entity = RetentionContact.builder()
                .retentionId(retentionId)
                .targetId(1001L)
                .managerId(managerId)
                .writerId(3003L)
                .reason("근속 지수 낮음")
                .createdAt(LocalDateTime.now())
                .isDeleted(UseStatus.N)
                .build();

        when(repository.findById(retentionId)).thenReturn(Optional.of(entity));

        RetentionContactResponseUpdateDto dto = RetentionContactResponseUpdateDto.builder()
                .retentionId(retentionId)
                .loginEmpId(managerId)
                .response(responseContent)
                .build();

        // when
        RetentionContactResponseUpdateResponse result = service.reportResponse(dto);

        // then
        assertThat(result.retentionId()).isEqualTo(retentionId);
        assertThat(result.response()).isEqualTo(responseContent);
        assertThat(result.responseAt()).isNotNull();
    }

    @Test
    @DisplayName("면담 보고 실패 - 이미 삭제된 요청")
    void reportResponse_alreadyDeleted_throwsException() {
        // given
        Long retentionId = 1L;
        Long managerId = 2002L;

        RetentionContact deleted = RetentionContact.builder()
                .retentionId(retentionId)
                .managerId(managerId)
                .isDeleted(UseStatus.Y)
                .build();

        when(repository.findById(retentionId)).thenReturn(Optional.of(deleted));

        RetentionContactResponseUpdateDto dto = RetentionContactResponseUpdateDto.builder()
                .retentionId(retentionId)
                .loginEmpId(managerId)
                .response("응답")
                .build();

        // when & then
        assertThatThrownBy(() -> service.reportResponse(dto))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_CONTACT_ALREADY_DELETED.getMessage());
    }

    @Test
    @DisplayName("면담 보고 실패 - manager가 아닌 사용자가 보고 시도")
    void reportResponse_notManager_throwsException() {
        // given
        Long retentionId = 1L;
        Long managerId = 2002L;
        Long otherUserId = 9999L;

        RetentionContact contact = RetentionContact.builder()
                .retentionId(retentionId)
                .managerId(managerId)
                .isDeleted(UseStatus.N)
                .build();

        when(repository.findById(retentionId)).thenReturn(Optional.of(contact));

        RetentionContactResponseUpdateDto dto = RetentionContactResponseUpdateDto.builder()
                .retentionId(retentionId)
                .loginEmpId(otherUserId)
                .response("응답")
                .build();

        // when & then
        assertThatThrownBy(() -> service.reportResponse(dto))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_CONTACT_RESPONSE_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("피드백 등록 성공")
    void giveFeedback_success() {
        // given
        Long retentionId = 1L;
        Long adminId = 9999L;
        String feedback = "면담 내용 확인, 보상 검토 예정";

        RetentionContact entity = RetentionContact.builder()
                .retentionId(retentionId)
                .managerId(2002L)
                .writerId(3003L)
                .reason("근속 문제")
                .isDeleted(UseStatus.N)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(retentionId)).thenReturn(Optional.of(entity));

        RetentionContactFeedbackUpdateDto dto = RetentionContactFeedbackUpdateDto.builder()
                .retentionId(retentionId)
                .loginEmpId(adminId)
                .feedback(feedback)
                .build();

        // when
        RetentionContactFeedbackUpdateResponse result = service.giveFeedback(dto);

        // then
        assertThat(result.retentionId()).isEqualTo(retentionId);
        assertThat(result.feedback()).isEqualTo(feedback);
    }

    @Test
    @DisplayName("피드백 등록 실패 - 삭제된 요청")
    void giveFeedback_alreadyDeleted_throwsException() {
        // given
        Long retentionId = 1L;
        RetentionContact deleted = RetentionContact.builder()
                .retentionId(retentionId)
                .isDeleted(UseStatus.Y)
                .build();

        when(repository.findById(retentionId)).thenReturn(Optional.of(deleted));

        RetentionContactFeedbackUpdateDto dto = RetentionContactFeedbackUpdateDto.builder()
                .retentionId(retentionId)
                .loginEmpId(9999L)
                .feedback("이미 삭제된 요청에 피드백")
                .build();

        // when & then
        assertThatThrownBy(() -> service.giveFeedback(dto))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_CONTACT_ALREADY_DELETED.getMessage());
    }

    @Test
    @DisplayName("피드백 등록 실패 - 요청 없음")
    void giveFeedback_notFound_throwsException() {
        // given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        RetentionContactFeedbackUpdateDto dto = RetentionContactFeedbackUpdateDto.builder()
                .retentionId(999L)
                .loginEmpId(9999L)
                .feedback("없는 요청")
                .build();

        // when & then
        assertThatThrownBy(() -> service.giveFeedback(dto))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_CONTACT_NOT_FOUND.getMessage());
    }


}
