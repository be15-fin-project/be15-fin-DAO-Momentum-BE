package com.dao.momentum.approve.command.application.service;

import com.dao.momentum.approve.command.application.dto.request.*;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategyDispatcher;
import com.dao.momentum.approve.command.domain.aggregate.*;
import com.dao.momentum.approve.command.domain.repository.*;
import com.dao.momentum.approve.exception.ApproveException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.kafka.dto.NotificationMessage;
import com.dao.momentum.common.kafka.producer.NotificationKafkaProducer;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveCommandServiceImplTest {

    @Mock
    private FormDetailStrategyDispatcher dispatcher;

    @Mock
    private ApproveRepository approveRepository;

    @Mock
    private ApproveLineRepository approveLineRepository;

    @Mock
    private ApproveLineListRepository approveLineListRepository;

    @Mock
    private ApproveRefRepository approveRefRepository;

    @Mock
    private FormDetailStrategy formDetailStrategy;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private NotificationKafkaProducer notificationKafkaProducer;


    @InjectMocks
    private ApproveCommandServiceImpl approveCommandService;

    @Test
    @DisplayName("결재 생성 성공 테스트")
    void createApprovalSuccess() throws Exception {
        ApproveRequest request = ApproveRequest.builder()
                .approveTitle("독서 동호회 신청")
                .approveType(ApproveType.PROPOSAL)
                .formDetail(mock(com.fasterxml.jackson.databind.JsonNode.class))
                .approveLineLists(List.of(
                        ApproveLineRequest.builder()
                                .approveLineOrder(1)
                                .isRequiredAll(IsRequiredAll.REQUIRED)
                                .approveLineList(List.of(
                                        ApproveLineListRequest.builder().empId(100L).build()))
                                .build()))
                .refRequests(List.of(
                        ApproveRefRequest.builder().empId(200L).build()))
                .build();

        when(employeeRepository.findByEmpId(1L))
                .thenReturn(Optional.of(Employee.builder()
                        .empId(1L)
                        .name("홍길동")
                        .build()));

        when(dispatcher.dispatch(ApproveType.PROPOSAL))
                .thenReturn(formDetailStrategy);
        when(formDetailStrategy.createNotificationContent(anyLong(), anyString()))
                .thenReturn("알림 내용입니다");
        doNothing().when(formDetailStrategy).saveDetail(any(), anyLong());

        ApproveLine firstLine = ApproveLine.builder().approveId(10L).build();
        when(approveLineRepository.findFirstLine(anyLong()))
                .thenReturn(Optional.of(firstLine));
        when(approveLineListRepository.findByApproveLineId(any()))
                .thenReturn(List.of(
                        ApproveLineList.builder().approveLineId(10L).empId(300L).build()
                ));

        doNothing().when(notificationKafkaProducer)
                .sendNotification(anyString(), any());

        when(approveRepository.save(any())).thenAnswer(invocation -> {
            Approve a = invocation.getArgument(0);
            Field field = a.getClass().getDeclaredField("approveId");
            field.setAccessible(true);
            field.set(a, 1L);
            return a;
        });

        approveCommandService.createApproval(request, 1L);

        verify(approveRepository, times(1)).save(any());
        verify(formDetailStrategy, times(1)).saveDetail(any(), eq(1L));
        verify(approveLineRepository, times(1)).save(any());
        verify(approveLineListRepository, times(1)).save(any());
        verify(approveRefRepository, times(1)).save(any());
        verify(notificationKafkaProducer, times(1))
                .sendNotification(eq("300"), any(NotificationMessage.class));
    }

    @Test
    @DisplayName("취소 결재인데 부모 ID 없으면 예외 발생")
    void createApprovalCancelWithoutParentThrows() {
        ApproveRequest request = ApproveRequest.builder()
                .approveType(ApproveType.CANCEL)
                .approveTitle("취소 테스트")
                .formDetail(mock(com.fasterxml.jackson.databind.JsonNode.class))
                .approveLineLists(List.of())
                .build();

        ApproveException exception = assertThrows(
                ApproveException.class,
                () -> approveCommandService.createApproval(request, 1L)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PARENT_APPROVE_ID_REQUIRED);
    }

    @Test
    @DisplayName("결재 문서 참조하기 테스트")
    void viewAsReferenceTest() {
        Long approveId = 1L;
        Long empId = 1L;

        ApproveRef approveRef = ApproveRef.builder()
                .empId(empId)
                .approveId(approveId)
                .isConfirmed(IsConfirmed.N)
                .build();

        when(approveRefRepository.getApproveRefByApproveIdAndEmpId(approveId, empId))
                .thenReturn(Optional.ofNullable(approveRef));


        approveCommandService.viewAsReference(approveId, empId);

        assert approveRef != null;
        assertEquals(IsConfirmed.Y, approveRef.getIsConfirmed(), "참조 확인 후 상태가 Y 이어야 합니다.");
    }

    @Test
    @DisplayName("참조자가 없을 경우 발생하는 에러")
    void viewAsReferenceNotFoundTest() {
        Long approveId = 1L;
        Long empId     = 2L;

        when(approveRefRepository.getApproveRefByApproveIdAndEmpId(approveId, empId))
                .thenReturn(Optional.empty());

        ApproveException exception = assertThrows(
                ApproveException.class,
                () -> approveCommandService.viewAsReference(approveId, empId)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_EXIST_REF);


    }
}