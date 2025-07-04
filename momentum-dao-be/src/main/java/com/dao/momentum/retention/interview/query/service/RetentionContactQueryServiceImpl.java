package com.dao.momentum.retention.interview.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.interview.exception.InterviewException;
import com.dao.momentum.retention.interview.query.dto.request.RetentionContactListRequestDto;
import com.dao.momentum.retention.interview.query.dto.response.RetentionContactDetailDto;
import com.dao.momentum.retention.interview.query.dto.response.RetentionContactItemDto;
import com.dao.momentum.retention.interview.query.dto.response.RetentionContactListResultDto;
import com.dao.momentum.retention.interview.query.mapper.RetentionContactMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetentionContactQueryServiceImpl implements RetentionContactQueryService {

    private final RetentionContactMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public RetentionContactListResultDto getContactList(RetentionContactListRequestDto req) {
        log.info(">>> getContactList called");
        RetentionContactListResultDto result = buildContactListResponse(req);
        log.info("Contact list fetched: {} items", result.getItems().size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public RetentionContactListResultDto getMyRequestedContactList(Long empId, RetentionContactListRequestDto req) {
        log.info(">>> getMyRequestedContactList called - empId={}", empId);
        req.setManagerId(empId);
        RetentionContactListResultDto result = buildContactListResponse(req);
        log.info("My requested contact list fetched: {} items", result.getItems().size());
        return result;
    }

    private RetentionContactListResultDto buildContactListResponse(RetentionContactListRequestDto req) {
        long total = mapper.countContacts(req);
        List<RetentionContactItemDto> items = mapper.findContacts(req);
        if (items == null) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND);
        }

        Pagination pagination = Pagination.builder()
                .currentPage(req.getPage())
                .totalItems(total)
                .totalPage((int) Math.ceil((double) total / req.getSize()))
                .build();

        return RetentionContactListResultDto.builder()
                .items(items)
                .pagination(pagination)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RetentionContactDetailDto getContactDetail(Long retentionId, Long requesterEmpId) {
        log.info(">>> getContactDetail called - retentionId={}, requesterEmpId={}", retentionId, requesterEmpId);
        RetentionContactDetailDto detail = mapper.findContactDetailById(retentionId);
        if (detail == null) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND);
        }

        boolean deletable = detail.getResponse() == null;
        boolean feedbackWritable = detail.getResponse() != null && detail.getFeedback() == null;

        RetentionContactDetailDto result = RetentionContactDetailDto.builder()
                .retentionId(detail.getRetentionId())
                .targetName(detail.getTargetName())
                .targetNo(detail.getTargetNo())
                .deptName(detail.getDeptName())
                .positionName(detail.getPositionName())
                .managerId(detail.getManagerId())
                .managerName(detail.getManagerName())
                .reason(detail.getReason())
                .createdAt(detail.getCreatedAt())
                .response(detail.getResponse())
                .responseAt(detail.getResponseAt())
                .feedback(detail.getFeedback())
                .deletable(deletable)
                .feedbackWritable(feedbackWritable)
                .build();

        log.info("Contact detail fetched - retentionId={}, deletable={}, feedbackWritable={}",
                result.getRetentionId(), result.isDeletable(), result.isFeedbackWritable());
        return result;
    }
}
