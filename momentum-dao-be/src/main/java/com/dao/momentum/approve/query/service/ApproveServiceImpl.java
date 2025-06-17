package com.dao.momentum.approve.query.service;

import com.dao.momentum.approve.exception.NotExistTabException;
import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.mapper.ApproveMapper;
import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApproveServiceImpl implements ApproveService {

    private final ApproveMapper approveMapper;

    /* 받은 결재 목록을 조회하는 메서드 */
    @Transactional(readOnly = true)
    public ApproveResponse getReceivedApprove(
            ApproveListRequest approveListRequest, Long empId, PageRequest pageRequest
    ) {

        // 존재하지 않는 결재 탭을 입력하는 경우 에러 처리
        List<String> validTabs = List.of("ATTENDANCE", "PROPOSAL", "RECEIPT", "CANCEL");

        if (!validTabs.contains(approveListRequest.getTab())) {
            throw new NotExistTabException(ErrorCode.NOT_EXIST_TAB);
        }

        // 멤버 존재 여부에 따른 에러 처리
        if(!approveMapper.existsByEmpId(empId)) {
            throw new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }

        // 받은 결재 문서 가져오기
        List<ApproveDTO> approveList
                = approveMapper.findReceivedApproval(approveListRequest, empId ,pageRequest);

        // 받은 결재 문서 개수 세기
        long total = approveMapper.countReceivedApproval(approveListRequest, empId);

        // 페이징 처리를 위한 부분
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();

        return ApproveResponse.builder()
                .approveDTO(approveList)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPage((int)Math.ceil((double)total/size))
                        .totalItems(total)
                        .build())
                .build();
    }


}
