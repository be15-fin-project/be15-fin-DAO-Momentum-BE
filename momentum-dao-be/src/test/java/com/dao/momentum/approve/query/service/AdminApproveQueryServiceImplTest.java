package com.dao.momentum.approve.query.service;

import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import com.dao.momentum.approve.exception.NotExistTabException;
import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.mapper.AdminApproveMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AdminApproveQueryServiceImplTest {

    @Mock
    private AdminApproveMapper adminApproveMapper;

    @InjectMocks
    private AdminApproveQueryServiceImpl adminApproveService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private List<ApproveDTO> getDummyApproves() {
        ApproveDTO dummy1 = ApproveDTO.builder()
                .approveId(1L)
                .parentApproveId(null)
                .statusType("ACCEPTED")
                .empId(1L)
                .approveTitle("점심 식사 영수증")
                .approveType(ApproveType.RECEIPT)
                .createAt(LocalDateTime.of(2025, 6, 1, 0, 0))
                .completeAt(null)
                .employeeName("장도윤")
                .departmentName("백엔드팀")
                .build();

        ApproveDTO dummy2 = ApproveDTO.builder()
                .approveId(2L)
                .parentApproveId(null)
                .statusType("ACCEPTED")
                .empId(2L)
                .approveTitle("출장 택시비")
                .approveType(ApproveType.RECEIPT)
                .createAt(LocalDateTime.of(2025, 6, 9, 0, 0))
                .completeAt(LocalDateTime.of(2025, 6, 13, 0, 0))
                .employeeName("김하윤")
                .departmentName("프론트엔드팀")
                .build();

        List<ApproveDTO> approveList = List.of(dummy1, dummy2);

        return approveList;
    }

    @Test
    @DisplayName("전체 결재 목록 조회 서비스 테스트")
    void getApproveListWithTwoDataTest() {
        ApproveListRequest request
                = com.dao.momentum.approve.query.dto.request.ApproveListRequest.builder()
                .tab("RECEIPT")
                .build();

        PageRequest pageRequest = new PageRequest(1, 10);
        List<ApproveDTO> dummyList = getDummyApproves();

        when(adminApproveMapper.findAllApproval(any(), any()))
                .thenReturn(dummyList);

        ApproveResponse result = adminApproveService.getApproveList(request, pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getApproveDTO().get(0).getApproveTitle()).isEqualTo("점심 식사 영수증");
        assertThat(result.getApproveDTO().get(1).getApproveType()).isEqualTo(ApproveType.RECEIPT);

        verify(adminApproveMapper, times(1)).findAllApproval(any(), any());

        result.getApproveDTO().forEach(System.out::println);
    }

    @Test
    @DisplayName("알 수 없는 탭이 들어왔을 때 발생하는 예외 테스트")
    void shouldThrowExceptionWhenTabIsInvalid() {
        ApproveListRequest request = ApproveListRequest.builder()
                .tab("INVALID_TAB")
                .build();

        PageRequest pageRequest = new PageRequest(1, 10);

        assertThrows(
                NotExistTabException.class,
                () -> adminApproveService.getApproveList(request, pageRequest)
        );
    }

}
