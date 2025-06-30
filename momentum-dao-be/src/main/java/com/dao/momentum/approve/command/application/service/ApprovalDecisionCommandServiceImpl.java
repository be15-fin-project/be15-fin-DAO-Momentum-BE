package com.dao.momentum.approve.command.application.service;

import com.dao.momentum.approve.command.application.dto.request.ApprovalConfirmRequest;
import com.dao.momentum.approve.command.domain.aggregate.*;
import com.dao.momentum.approve.command.domain.repository.ApproveLineListRepository;
import com.dao.momentum.approve.command.domain.repository.ApproveLineRepository;
import com.dao.momentum.approve.command.domain.repository.ApproveRepository;
import com.dao.momentum.approve.exception.ApproveException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.command.application.service.WorkApplyCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalDecisionCommandServiceImpl implements ApprovalDecisionCommandService{

    private final WorkApplyCommandService workApplyCommandService;
    private final ApprovalCancelCommandService approvalCancelCommandService;
    private final ApproveRepository approveRepository;
    private final ApproveLineRepository approveLineRepository;
    private final ApproveLineListRepository approveLineListRepository;

    private static final int PENDING = 1;
    private static final int APPROVED = 2;
    private static final int REJECTED = 3;

    private static final Set<ApproveType> WORK_APPROVE_TYPE = Set.of(
            ApproveType.BUSINESSTRIP,
            ApproveType.OVERTIME,
            ApproveType.REMOTEWORK,
            ApproveType.VACATION,
            ApproveType.WORKCORRECTION
    );

    @Transactional
    public void approveOrReject(ApprovalConfirmRequest approvalConfirmRequest, Long emdId){
        Long approveLineListId = approvalConfirmRequest.getApproveLineListId();
        String isApproved = approvalConfirmRequest.getIsApproved();
        String reason = approvalConfirmRequest.getReason();

        log.info("결재선 목록(결재자) : {}", approveLineListId);
        log.info("승인/반려 여부 : {}", isApproved);
        log.info("승인/반려 사유 : {}", reason);

        // 1. 결재 관련 정보 가져오기
        // 1-1. 결재선 목록(사람) 가져오기
        ApproveLineList approvalAssignee = approveLineListRepository
                .getApproveLineListById(approveLineListId)
                .orElseThrow(() -> new ApproveException(ErrorCode.NOT_EXIST_APPROVE_LINE_LIST));

        Long approveLineId = approvalAssignee.getApproveLineId(); // 결재선 아이디
        log.info("결재선 아이디 : {}", approveLineId);

        // 접근할 수 없는 결재인 경우 (내가 결재선으로 지정된 결재가 아닌 경우)
        if(!Objects.equals(approvalAssignee.getEmpId(), emdId)) {
            log.info("접근할 수 없는 결재입니다.");
            throw new ApproveException(ErrorCode.APPROVAL_ACCESS_DENIED);
        }

        // 1-2. 어떤 결재선인지 아이디 가져오기
        ApproveLine approveLine = approveLineRepository
                .getApproveLineById(approveLineId)
                .orElseThrow(() -> new ApproveException(ErrorCode.NOT_EXIST_APPROVE_LINE));

        Long approveId = approveLine.getApproveId(); // 결재 아이디

        log.info("결재 아이디 : {}", approveId);

        // 대기 중인 상태가 아니라면 승인/반려 할 필요가 없음
        if(approveLine.getStatusId() != PENDING) {
            log.info("승인/반려된 결재선 입니다.");
            throw new ApproveException(ErrorCode.APPROVAL_LINE_ALREADY_PROCESSED);
        }

        // 이전 결재선이 승인되지 않은 경우에는 예외 처리
        List<ApproveLine> previousLines = approveLineRepository
                .getApproveLinesBeforeOrder(approveId, approveLine.getApproveLineOrder());

        // 이전 결재가 승인된 상태가 아니라면 결재를 진행할 수 없음
        boolean hasPendingOrRejected = previousLines.stream()
                .anyMatch(line -> line.getStatusId() != APPROVED);

        if (hasPendingOrRejected) {
            throw new ApproveException(ErrorCode.PREVIOUS_APPROVAL_NOT_COMPLETED);
        }

        // 1-3. 어떤 결재인지 아이디 가져오기
        Approve approve = approveRepository
                .getApproveByApproveId(approveId)
                .orElseThrow(() -> new ApproveException(ErrorCode.NOT_EXIST_APPROVE));

        // 대기 중인 상태가 아니라면 승인/반려 할 필요가 없음
        if(approve.getStatusId() != PENDING) {
            log.info("승인/반려된 결재 입니다.");
            throw new ApproveException(ErrorCode.APPROVAL_ALREADY_PROCESSED);
        }

        // 2. 승인/반려 여부에 따라 각각 메서드 작성하기
        if(isApproved.equals("승인")) { // 승인 결재인 경우
            approve(approvalAssignee, approveLine, approve, reason);
        } else { // 반려 결재인 경우
            if(reason.isEmpty()) { // 결재 사유가 없는 경우 발생하는 에러
                log.info("결재 반려 사유를 반드시 입력해야 합니다.");
                throw new ApproveException(ErrorCode.MISSING_APPROVAL_REASON);
            }
            reject(approvalAssignee, approveLine, approve, reason);
        }
    }

    /* 승인한 경우 */
    private void approve(
            ApproveLineList approvalAssignee,
            ApproveLine approveLine,
            Approve approve,
            String reason

    ) {
        // 1. 결재선 목록 (결재자) 승인 상태로 변경
        approvalAssignee.updateApproveLineListStatus(APPROVED);

        // 2. 승인 사유가 있다면 승인 사유 넣기
        if(!reason.isEmpty()) {
            approvalAssignee.updateReason(reason);
        }

        // 3. 결재선의 필수/선택 여부에 따른 분류
        updateApproveLineStatusIfCompleted(approveLine);

        // 4. 만약에 승인된 결재선이 마지막 결재선이라면 결재를 승인으로 처리하기
        updateApproveStatusIfFinalApprovalLine(approve);
    }

    /* 결재선 승인 여부 확인 */
    private void updateApproveLineStatusIfCompleted(ApproveLine approveLine) {
        // 선택 결재선은 한 명만 승인해도 승인 처리
        if (approveLine.getIsRequiredAll() == IsRequiredAll.OPTIONAL) {
            approveLine.updateApproveLineStatus(APPROVED);
            notifyNextApproveLineIfExists(approveLine); // 다음 결재선이 있다면 알림
        } else if (approveLine.getIsRequiredAll() == IsRequiredAll.REQUIRED) {
            // 필수 결재선은 모두 승인해야 승인 처리 (결재선 아이디로 결재 목록 가져오기)
            List<Integer> statusIds = approveLineListRepository
                    .getAssigneeStatusByApproveLineId(approveLine.getId());

            // 모든 결재자가 승인했는지 확인
            boolean allApproved = statusIds.stream()
                    .allMatch(statusId -> statusId == APPROVED);

            if (allApproved) {
                approveLine.updateApproveLineStatus(APPROVED);
                notifyNextApproveLineIfExists(approveLine); // 다음 결재선이 있다면 알림
            }
        }
    }

    /* 다음 결재선 사람에게 알림*/
    private void notifyNextApproveLineIfExists(ApproveLine currentLine) {
        approveLineRepository.findNextLine(
                currentLine.getApproveId(),
                currentLine.getApproveLineOrder()       
        ).ifPresent(nextLine -> {
            // 다음 결재선에 속한 결재자 전부 조회 하기
            List<ApproveLineList> assignees =
                    approveLineListRepository.findByApproveLineId(nextLine.getId());

            assignees.forEach(a -> {
                /* 여기에서 알림 전송하면 됨 */
                log.info("다음 결재선 사람에게 알림 보내기");
            });
        });
    }

    /* 마지막 결재선이 승인되었다면 결재를 승인으로 처리*/
    private void updateApproveStatusIfFinalApprovalLine(Approve approve) {
        List<Integer> approveLines = approveLineRepository.getApproveLinesByApproveId(approve.getApproveId());

        boolean allApproved = approveLines.stream()
                .allMatch(statusId -> statusId == APPROVED);

        if (allApproved) {
            approve.updateApproveStatus(APPROVED);

            log.info("모든 결재선이 승인되어, 결재 {}이(가) 승인 처리됨", approve.getApproveId());

            ApproveType approveType = approve.getApproveType();
            if(WORK_APPROVE_TYPE.contains(approveType)) { // 근태 결재인 경우
                workApplyCommandService.applyApprovalWork(approve);
            } else if(approveType == ApproveType.CANCEL) { // 취소 결재인 경우
                Long parentApproveId = approve.getParentApproveId();
                
                // 부모 결재 가져오기
                Approve parentApprove = approveRepository
                        .getApproveByApproveId(parentApproveId)
                        .orElseThrow(() -> new ApproveException(ErrorCode.NOT_EXIST_APPROVE));

                // 결재 취소 시간 넣기
                parentApprove.insertCancelDate();

                approvalCancelCommandService.cancelApprovalWork(parentApprove);
            }
        }
    }

    /* 반려된 경우 작성되는 메소드 */
    private void reject(
            ApproveLineList approvalAssignee,
            ApproveLine approveLine,
            Approve approve,
            String reason
    ) {
        // 결재자, 결재선, 결재내역 모두 상태를 반려 상태로 변경
        approvalAssignee.updateApproveLineListStatus(REJECTED); // 결재 상태로 변경
        approvalAssignee.updateReason(reason); // 반려 사유 설정

        approveLine.updateApproveLineStatus(REJECTED);
        approve.updateApproveStatus(REJECTED);
    }

}
