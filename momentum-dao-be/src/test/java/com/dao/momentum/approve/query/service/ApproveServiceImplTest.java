package com.dao.momentum.approve.query.service;

import com.dao.momentum.approve.exception.NotExistTabException;
import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.mapper.ApproveMapper;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;

class ApproveServiceImplTest {

    @Mock
    private ApproveMapper approveMapper;

    @InjectMocks
    private ApproveServiceImpl approveService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private List<ApproveDTO> getDummyApproves() {
        ApproveDTO dummy1 = ApproveDTO.builder()
                .approveId(1L)
                .parentApproveId(null)
                .statusType("ACCEPTED")
                .empId(1L)
                .approveTitle("점심 식사 영수증")
                .approveType("RECEIPT")
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
                .approveType("RECEIPT")
                .createAt(LocalDateTime.of(2025, 6, 9, 0, 0))
                .completeAt(LocalDateTime.of(2025, 6, 13, 0, 0))
                .employeeName("김하윤")
                .departmentName("프론트엔드팀")
                .build();


        return List.of(dummy1, dummy2);
    }

    @Test
    @DisplayName("받은 결재 목록 조회 테스트")
    void getReceivedApproveTest() {
        ApproveListRequest request = ApproveListRequest.builder()
                .tab("RECEIPT")
                .build();

        PageRequest pageRequest = new PageRequest(1, 10);
        Long empId = 1L;

        List<ApproveDTO> dummyList = getDummyApproves();

        when(approveMapper.existsByEmpId(empId)).thenReturn(true);
        when(approveMapper.findReceivedApproval(request, empId, pageRequest)).thenReturn(dummyList);
        when(approveMapper.countReceivedApproval(request, empId)).thenReturn((long) dummyList.size());

        ApproveResponse response = approveService.getReceivedApprove(request, empId, pageRequest);

        assertThat(response).isNotNull();
        assertThat(response.getApproveDTO()).hasSize(2);
        assertThat(response.getPagination().getTotalItems()).isEqualTo(2);
        assertThat(response.getPagination().getTotalPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 탭이 들어왔을 때 예외가 발생하는 테스트")
    void getReceivedApproveInvalidTabTest() {
        ApproveListRequest request = ApproveListRequest.builder()
                .tab("INVALID_TAB")
                .build();

        PageRequest pageRequest = new PageRequest(1, 10);
        Long empId = 1L;

        assertThrows(NotExistTabException.class,
                () -> approveService.getReceivedApprove(request, empId, pageRequest));
    }

    @Test
    @DisplayName("존재하지 않는 사원 ID가 들어왔을 때 예외 발생")
    void getReceivedApproveInvalidEmpIdTest() {
        ApproveListRequest request = ApproveListRequest.builder()
                .tab("RECEIPT")
                .build();

        PageRequest pageRequest = new PageRequest(1, 10);
        Long empId = 999L;

        when(approveMapper.existsByEmpId(empId)).thenReturn(false);

        assertThrows(EmployeeException.class,
                () -> approveService.getReceivedApprove(request, empId, pageRequest));
    }
}
