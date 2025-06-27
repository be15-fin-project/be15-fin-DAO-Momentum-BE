package com.dao.momentum.approve.command.application.service;

import com.dao.momentum.approve.command.application.dto.request.ApprovalConfirmRequest;
import org.springframework.stereotype.Service;

@Service
public interface ApprovalDecisionCommandService {
    
    void approveOrReject(ApprovalConfirmRequest approvalConfirmRequest, Long empId);


}
