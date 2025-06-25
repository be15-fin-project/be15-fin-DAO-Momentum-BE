package com.dao.momentum.approve.command.application.service.strategy;

import com.dao.momentum.approve.command.application.service.strategy.form.*;
import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import com.dao.momentum.approve.exception.NotFoundApproveException;
import com.dao.momentum.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/* 결재 폼을 분배 해주는 클래스 (전략을 결정하는 클래스) */
@Component
@RequiredArgsConstructor
public class FormDetailStrategyDispatcher {

    private final BusinessTripFormStrategy businessTripFormStrategy;
    private final CancelFormStrategy cancelFormStrategy;
    private final OvertimeFormStrategy overtimeFormStrategy;
    private final ProposalFormStrategy proposalFormStrategy;
    private final RemoteWorkFormStrategy remoteWorkFormStrategy;
    private final VacationFormStrategy vacationFormStrategy;
    private final WorkCorrectionFormStrategy workCorrectionFormStrategy;
    private final ReceiptFormStrategy receiptFormStrategy;

    public FormDetailStrategy dispatch(ApproveType type) {
        return switch (type) {
            case BUSINESSTRIP -> businessTripFormStrategy;
            case CANCEL -> cancelFormStrategy;
            case OVERTIME -> overtimeFormStrategy;
            case REMOTEWORK -> remoteWorkFormStrategy;
            case VACATION -> vacationFormStrategy;
            case WORKCORRECTION -> workCorrectionFormStrategy;
            case PROPOSAL -> proposalFormStrategy;
            case RECEIPT -> receiptFormStrategy;
            default -> throw new NotFoundApproveException(ErrorCode.NOT_EXIST_APPROVE);
        };
    }
}