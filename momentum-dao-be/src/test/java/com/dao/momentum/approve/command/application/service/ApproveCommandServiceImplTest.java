package com.dao.momentum.approve.command.application.service;

import com.dao.momentum.approve.command.application.dto.request.*;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategyDispatcher;
import com.dao.momentum.approve.command.domain.aggregate.*;
import com.dao.momentum.approve.command.domain.repository.*;
import com.dao.momentum.approve.exception.ApproveException;
import com.dao.momentum.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private ApproveCommandServiceImpl approveCommandService;

    @Test
    @DisplayName("결재 생성 성공 테스트")
    void createApprovalSuccess() {
        ApproveRequest request = ApproveRequest.builder()
                .approveTitle("독서 동호회 신청")
                .approveType(ApproveType.PROPOSAL)
                .formDetail(mock(com.fasterxml.jackson.databind.JsonNode.class))
                .approveLineLists(List.of(ApproveLineRequest.builder()
                        .approveLineOrder(1)
                        .isRequiredAll(IsRequiredAll.REQUIRED)
                        .approveLineList(List.of(ApproveLineListRequest.builder().empId(100L).build()))
                        .build()))
                .refRequests(List.of(ApproveRefRequest.builder().empId(200L).build()))
                .build();

        when(dispatcher.dispatch(ApproveType.PROPOSAL)).thenReturn(formDetailStrategy);

        doNothing().when(formDetailStrategy).saveDetail(any(), anyLong());

        when(approveRepository.save(any())).thenAnswer(invocation -> {
            Approve a = invocation.getArgument(0);

            // 리플렉션으로 approveId 필드 강제 설정 (테스트에서만 사용되기 때문에 실제 코드에는 영향이 없는 부분)
            java.lang.reflect.Field field = a.getClass().getDeclaredField("approveId");
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