package com.dao.momentum.approve.query.service;

import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;

public interface ApproveService {

    ApproveResponse getReceivedApprove(
            ApproveListRequest approveListRequest,
            Long empId,
            PageRequest pageRequest
    );

}
