package com.dao.momentum.approve.query.service;

import com.dao.momentum.approve.query.dto.EmployeeLeaderDto;
import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.DraftApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveDetailResponse;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.dto.response.DraftApproveResponse;

public interface ApproveQueryService {

    /* 받는 문서를 조회하는 메서드 */
    ApproveResponse getReceivedApprove(
            ApproveListRequest approveListRequest,
            Long empId,
            PageRequest pageRequest
    );

    /* 보낸 문서를 조회하는 메서드 */
    DraftApproveResponse getDraftApprove(
            DraftApproveListRequest draftApproveListRequest,
            Long empId,
            PageRequest pageRequest
    );

    /* 결재 상세 조회 메서드 */
    ApproveDetailResponse getApproveDetail(Long approveId);


    /* 사원의 팀장 조회 메서드 */
    EmployeeLeaderDto getEmployeeLeader(Long empId);

}
