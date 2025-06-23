package com.dao.momentum.retention.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.exception.RetentionException;
import com.dao.momentum.retention.query.dto.request.RetentionContactListRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionContactItemDto;
import com.dao.momentum.retention.query.dto.response.RetentionContactListResultDto;
import com.dao.momentum.retention.query.mapper.RetentionContactMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class RetentionContactQueryServiceImplTest {

    @Mock
    private RetentionContactMapper mapper;

    @InjectMocks
    private RetentionContactQueryServiceImpl service;

    private RetentionContactListRequestDto req;

    @BeforeEach
    void setUp() {
        req = new RetentionContactListRequestDto();
        req.setPage(1);
        req.setSize(10);
    }

    @Test
    @DisplayName("면담 기록 전체 조회 성공")
    void getContactList_success() {
        // given
        RetentionContactItemDto item = RetentionContactItemDto.builder()
                .createdAt(LocalDateTime.now())
                .empNo("20250020")
                .targetName("이하준")
                .deptName("프론트엔드팀")
                .positionName("대리")
                .managerName("정지우")
                .reason("복지 불만 비율이 높아짐")
                .build();

        when(mapper.countContacts(req)).thenReturn(1);
        when(mapper.findContacts(req)).thenReturn(List.of(item));

        // when
        RetentionContactListResultDto result = service.getContactList(req);

        // then
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getPagination().getTotalItems()).isEqualTo(1L);
        assertThat(result.getPagination().getTotalPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("면담 기록 조회 실패 - 결과 NULL")
    void getContactList_null_throwsException() {
        when(mapper.countContacts(req)).thenReturn(1);
        when(mapper.findContacts(req)).thenReturn(null);

        assertThatThrownBy(() -> service.getContactList(req))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_CONTACT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("나에게 요청된 면담 기록 조회 성공")
    void getMyRequestedContactList_success() {
        Long empId = 34L;

        RetentionContactItemDto item = RetentionContactItemDto.builder()
                .createdAt(LocalDateTime.now())
                .empNo("20250020")
                .targetName("이하준")
                .deptName("프론트엔드팀")
                .positionName("대리")
                .managerName("정지우")
                .reason("복지 불만 비율이 높아짐")
                .build();

        req.setManagerId(empId);

        when(mapper.countContacts(req)).thenReturn(1);
        when(mapper.findContacts(req)).thenReturn(List.of(item));

        RetentionContactListResultDto result = service.getMyRequestedContactList(empId, req);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getManagerName()).isEqualTo("정지우");
    }
}
