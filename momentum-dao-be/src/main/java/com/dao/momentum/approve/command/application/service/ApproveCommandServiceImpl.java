package com.dao.momentum.approve.command.application.service;

import com.dao.momentum.approve.command.application.dto.request.*;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategy;
import com.dao.momentum.approve.command.application.service.strategy.FormDetailStrategyDispatcher;
import com.dao.momentum.approve.command.domain.aggregate.*;
import com.dao.momentum.approve.command.domain.repository.*;
import com.dao.momentum.approve.exception.ApproveException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.kafka.dto.NotificationMessage;
import com.dao.momentum.common.kafka.producer.NotificationKafkaProducer;
import com.dao.momentum.file.command.application.dto.request.AttachmentRequest;
import com.dao.momentum.file.command.domain.aggregate.File;
import com.dao.momentum.file.command.domain.repository.FileRepository;
import com.dao.momentum.notification.command.domain.repository.NotificationRepository;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.work.command.domain.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApproveCommandServiceImpl implements ApproveCommandService{

    private final FormDetailStrategyDispatcher formDetailStrategyDispatcher;
    private final ApproveRepository approveRepository;
    private final ApproveLineRepository approveLineRepository;
    private final ApproveLineListRepository approveLineListRepository;
    private final ApproveRefRepository approveRefRepository;

    private final ApproveReceiptRepository receiptRepository;
    private final ApproveProposalRepository proposalRepository;
    private final ApproveCancelRepository cancelRepository;
    private final WorkCorrectionRepository workCorrectionRepository;
    private final OvertimeRepository overtimeRepository;
    private final RemoteWorkRepository remoteWorkRepository;
    private final VacationRepository vacationRepository;
    private final BusinessTripRepository businessTripRepository;

    private final FileRepository fileRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationKafkaProducer notificationKafkaProducer;
    private final NotificationRepository notificationRepository;

    /*
    * 결재 문서를 작성하는 메소드
    * */
    @Transactional
    public void createApproval(ApproveRequest approveRequest, Long empId) {
        // ApproveRequest 에 들어 있는 값 가져오기
        Long parentApproveId = approveRequest.getParentApproveId();
        ApproveType approveType = approveRequest.getApproveType();
        String title = approveRequest.getApproveTitle();
        JsonNode form = approveRequest.getFormDetail();

        log.info("부모 결재 Id : {} ", parentApproveId);
        log.info("결재 종류 : {}", approveType);
        log.info("결재 문제 제목 : {}", title);
        log.info("결재 문서 내용 : {}", form);
        log.info("결재선 목록 : {}", approveRequest.getApproveLineLists());
        log.info("참조자 목록 : {}", approveRequest.getRefRequests());
        log.info("첨부 파일 : {}", approveRequest.getAttachments());

        // 취소 요청인 경우에는 parentApproveId가 있어야 함
        if(approveType == ApproveType.CANCEL) {
            if(parentApproveId == null) {
                throw new ApproveException(ErrorCode.PARENT_APPROVE_ID_REQUIRED);
            }
        }

        // 1. 결재 생성하기
        Approve approve = Approve.builder()
                .parentApproveId(parentApproveId)
                .approveTitle(title)
                .approveType(approveType)
                .empId(empId)
                .build();

        approveRepository.save(approve);

        Long approveId = approve.getApproveId(); // 결재 아이디

        log.info("결재 아이디 : {}", approveId);

        // 2. 결재 문서 저장하기
        // 어떤 결재 문서로 저장할지 결정하기
        FormDetailStrategy strategy = formDetailStrategyDispatcher.dispatch(approveType);

        strategy.saveDetail(form, approveId);

        // 3.첨부파일 S3 업로드 및 DB 저장
        List<AttachmentRequest> attachments = approveRequest.getAttachments();

        // 영수증 결재에는 반드시 첨부파일이 있어야 함
        if (approveType == ApproveType.RECEIPT) {
            if (attachments == null || attachments.isEmpty()) {
                throw new ApproveException(ErrorCode.RECEIPT_IMAGE_REQUIRED);
            }
        }

        if (attachments != null && !attachments.isEmpty()) {
            for (AttachmentRequest attachment : attachments) {
                File file = File.builder()
                        .announcementId(null)
                        .approveId(approveId)
                        .contractId(null)
                        .name(attachment.getName())
                        .s3Key(attachment.getS3Key())
                        .type(attachment.getType())
                        .build();
                fileRepository.save(file);
            }
        }

        // 4. 결재선 저장하기
        createApproveLine(approveId, approveRequest.getApproveLineLists());

        // 5. 참조인 저장하기
        List<ApproveRefRequest> approveRefRequests =
                Optional.ofNullable(approveRequest.getRefRequests()).orElse(List.of());

        if (!approveRefRequests.isEmpty()) {
            createApproveRef(approveId, approveRefRequests);
        }

        notifyFirstApproveLine(approve);

    }

    /*
     * 참조인이 결재 내역을 확인하는 메소드
     * */
    @Transactional
    public void viewAsReference(Long approveId, Long empId) {
        // 1. 결재 아이디와 참조 사원 Id로 결재 참조 내역 불러오기
       ApproveRef approveRef = approveRefRepository.getApproveRefByApproveIdAndEmpId(approveId, empId)
               .orElseThrow(() -> new ApproveException(ErrorCode.NOT_EXIST_REF));

       log.info("해당 참조의 결재 아이디 : {},  해당 참조의 사원 아이디 : {}", approveId, empId);

        // 2. 참조 테이블 참조 상태 변경하기
        approveRef.updateRefStatus();
    }

    /*
    * 결재 문서 삭제하기 (모든 결재자가 확인하지 않은 경우 : '대기' 상태인 경우)
    * */
    @Transactional
    public void deleteApproval(Long approveId, Long empId) {

        log.info("삭제 하려는 결재 아이디 : {},  문서를 작성한 사원 ID : {}", approveId, empId);

        Approve approve = approveRepository.getApproveByApproveId(approveId)
                .orElseThrow(() -> new ApproveException(ErrorCode.NOT_EXIST_APPROVE));

        if(!Objects.equals(approve.getEmpId(), empId)) {
            throw new ApproveException(ErrorCode.NO_DELETE_PERMISSION);
        }

        // 1. 결재선 조회하기
        List<Long> approveLineIds
                = approveLineRepository.getApproveLinesByApproveId(approveId);

        // 2. 결재선에 지정되어 있는 결재자의 상태 조회하기
        List<Integer> statusIds = approveLineListRepository
                .getAllStatusesByApproveLineIds(approveLineIds);

        // 3. 모든 결재자가 대기 상태인지 확인
        boolean allPending = statusIds.stream()
                .allMatch(statusId -> statusId == 1);

        if (!allPending) {
            throw new ApproveException(ErrorCode.ALREADY_START_APPROVAL);
        }

        // 4. 결재 관련 문서(8가지) 삭제하기
        deleteDetailByType(approve.getApproveType(), approveId);

        // 5. 결재선 결재자 삭제하기
        for(Long approveLineId : approveLineIds) {
            approveLineListRepository.deleteApproveLineListByApproveLineId(approveLineId);
        }

        // 6. 결재선 삭제하기
        approveLineRepository.deleteApproveLinesByApproveId(approveId);

        // 7. 참조자 삭제하기
        approveRefRepository.deleteApproveRefByApprovalId(approveId);

        // 8. 파일 삭제하기
        fileRepository.deleteByApprovalId(approveId);

        // 9. 알림 내역 삭제하기
        notificationRepository.deleteNotificationByApproveId(approveId);

        // 10. 결재 삭제하기
        approveRepository.deleteApproveByApproveId(approveId);
    }

    /*
    * 결재 문서 수정하기
    * */
    @Transactional
    public void updateApproval(ApproveRequest approveRequest, Long approveId, Long empId) {

        log.info("수정 하려는 결재 아이디 : {},  문서를 작성한 사원 ID : {}", approveId, empId);

        Approve approve = approveRepository.getApproveByApproveId(approveId)
                .orElseThrow(() -> new ApproveException(ErrorCode.NOT_EXIST_APPROVE));

        ApproveType approveType = approveRequest.getApproveType();

        approve.updateTitle(approveRequest.getApproveTitle());

        if(!Objects.equals(approve.getEmpId(), empId)) {
            throw new ApproveException(ErrorCode.NO_UPDATE_PERMISSION);
        }

        List<Long> approveLineIds
                = approveLineRepository.getApproveLinesByApproveId(approveId);

        List<Integer> statusIds = approveLineListRepository
                .getAllStatusesByApproveLineIds(approveLineIds);

        boolean allPending = statusIds.stream()
                .allMatch(statusId -> statusId == 1);

        if (!allPending) {
            throw new ApproveException(ErrorCode.ALREADY_START_APPROVAL);
        }

        deleteDetailByType(approve.getApproveType(), approveId);

        for(Long approveLineId : approveLineIds) {
            approveLineListRepository.deleteApproveLineListByApproveLineId(approveLineId);
        }

        approveLineRepository.deleteApproveLinesByApproveId(approveId);

        approveRefRepository.deleteApproveRefByApprovalId(approveId);

        fileRepository.deleteByApprovalId(approveId);

        FormDetailStrategy strategy = formDetailStrategyDispatcher.dispatch(approveType);

        strategy.saveDetail(approveRequest.getFormDetail(), approveId);

        List<AttachmentRequest> attachments = approveRequest.getAttachments();

        if (approveType == ApproveType.RECEIPT) {
            if (attachments == null || attachments.isEmpty()) {
                throw new ApproveException(ErrorCode.RECEIPT_IMAGE_REQUIRED);
            }
        }

        if (attachments != null && !attachments.isEmpty()) {
            for (AttachmentRequest attachment : attachments) {
                File file = File.builder()
                        .announcementId(null)
                        .approveId(approveId)
                        .contractId(null)
                        .name(attachment.getName())
                        .s3Key(attachment.getS3Key())
                        .type(attachment.getType())
                        .build();
                fileRepository.save(file);
            }
        }

        createApproveLine(approveId, approveRequest.getApproveLineLists());

        List<ApproveRefRequest> approveRefRequests =
                Optional.ofNullable(approveRequest.getRefRequests()).orElse(List.of());

        if (!approveRefRequests.isEmpty()) {
            createApproveRef(approveId, approveRefRequests);
        }
    }

    /*
     * 결재선 생성하기 (결재선, 결재자 목록)
     * */
    private void createApproveLine(Long approveId, List<ApproveLineRequest> approveLineRequests) {
        // 결재선은 여러 개 존재하기 때문에 반복문을 이용해 저장
        for (ApproveLineRequest lineRequest : approveLineRequests) {
            // 결재선 생성 후 저장하기
            ApproveLine approveLine = ApproveLine.builder()
                    .approveId(approveId)
                    .approveLineOrder(lineRequest.getApproveLineOrder())
                    .isRequiredAll(lineRequest.getIsRequiredAll())
                    .build();

            approveLineRepository.save(approveLine);

            Long approveLineId = approveLine.getId();

            // 결재자 목록 저장하기
            for (ApproveLineListRequest listRequest : lineRequest.getApproveLineList()) {
                ApproveLineList lineList = ApproveLineList.builder()
                        .approveLineId(approveLineId)
                        .empId(listRequest.getEmpId())
                        .build();

                approveLineListRepository.save(lineList);
            }
        }
    }

    /*
    * 참조인 생성하기
    * */
    private void createApproveRef(Long approveId, List<ApproveRefRequest> approveRefRequests) {
        // 참조인은 여러명 이기 때문에 반복문을 이용해 저장
        for (ApproveRefRequest refRequest : approveRefRequests) {
            ApproveRef approveRef = ApproveRef.builder()
                    .approveId(approveId)
                    .empId(refRequest.getEmpId())
                    .isConfirmed(IsConfirmed.N)
                    .build();

            approveRefRepository.save(approveRef);
        }
    }

    /* 첫번째 결재선(결재선 번호가 1) 사람에게 알림 보내기 */
    private void notifyFirstApproveLine(Approve approve) {
        Long approveId = approve.getApproveId();
        Long senderId = approve.getEmpId();

        // 1. 발신자 이름 조회
        Employee sender = employeeRepository.findByEmpId(senderId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));
        String senderName = sender.getName();

        // 2. 결재 타입
        ApproveType approveType = approve.getApproveType();

        // 3. 전략에 따른 content 생성
        FormDetailStrategy strategy = formDetailStrategyDispatcher.dispatch(approveType);
        String content = strategy.createNotificationContent(approveId, senderName, FormDetailStrategy.NotificationType.REQUEST);

        // 4. 첫 번째 결재선 알림 전송
        approveLineRepository.findFirstLine(approveId)
                .ifPresent(firstLine -> {

                    List<ApproveLineList> assignees =
                            approveLineListRepository.findByApproveLineId(firstLine.getId());

                    assignees.forEach(assignee -> {
                        NotificationMessage message = NotificationMessage.builder()
                                .content(content)
                                .type("APPROVAL_REQUEST")
                                .url("/approval/detail/" + approve.getApproveId() + "?from=inbox&tab=received")
                                .receiverId(assignee.getEmpId())
                                .senderId(senderId)
                                .senderName(senderName)
                                .timestamp(LocalDateTime.now())
                                .approveId(approveId)
                                .build();

                        try {
                            notificationKafkaProducer.sendNotification(
                                    assignee.getEmpId().toString(),
                                    message
                            );
                            log.info("결재 ID: {}, 수신자 ID: {} → 첫 결재선 알림 전송 완료",
                                    approveId, assignee.getEmpId());
                        } catch (Exception e) {
                            log.error("알림 전송 실패 - 결재 ID: {}, 수신자 ID: {}, 사유: {}",
                                    approveId, assignee.getEmpId(), e.getMessage(), e);
                        }
                    });
                });
    }

    /*
    * type에 맞춰서 결재 문서 삭제하기
    * */
    private void deleteDetailByType(ApproveType type, Long approveId) {
        switch (type) {
            case RECEIPT -> receiptRepository.deleteApproveReceiptByApprovalId(approveId);
            case PROPOSAL -> proposalRepository.deleteApproveProposalByApprovalId(approveId);
            case CANCEL -> cancelRepository.deleteApproveCancelByApproveId(approveId);
            case WORKCORRECTION -> workCorrectionRepository.deleteWorkCorrectionByApproveId(approveId);
            case OVERTIME -> overtimeRepository.deleteOvertimeByApproveId(approveId);
            case REMOTEWORK -> remoteWorkRepository.deleteRemoteWorkByApproveId(approveId);
            case VACATION -> vacationRepository.deleteVacationByApproveId(approveId);
            case BUSINESSTRIP -> businessTripRepository.deleteBusinessTripByApproveId(approveId);
        }
    }


}
