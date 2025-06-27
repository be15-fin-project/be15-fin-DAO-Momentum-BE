package com.dao.momentum.approve.command.application.service;

import com.dao.momentum.approve.command.application.dto.request.ApproveRequest;
import org.springframework.stereotype.Service;

@Service
public interface ApproveCommandService {

    void createApproval(ApproveRequest approveRequest, Long empId);

    void viewAsReference(Long approveId, Long empId);

}
