package com.dao.momentum.retention.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.command.application.dto.request.RetentionContactCreateDto;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactResponse;
import com.dao.momentum.retention.command.domain.aggregate.RetentionContact;
import com.dao.momentum.retention.command.domain.repository.RetentionContactRepository;
import com.dao.momentum.retention.exception.RetentionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

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

}
