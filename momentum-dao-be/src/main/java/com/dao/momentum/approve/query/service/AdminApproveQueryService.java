package com.dao.momentum.approve.query.service;

import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import org.springframework.stereotype.Service;


@Service
public interface AdminApproveQueryService {

    ApproveResponse getApproveList(
            ApproveListRequest approveListRequest,
            PageRequest pageRequest
    );

}
