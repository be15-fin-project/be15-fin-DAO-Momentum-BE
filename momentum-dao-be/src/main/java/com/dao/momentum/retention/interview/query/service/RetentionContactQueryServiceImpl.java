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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetentionContactQueryServiceImpl implements RetentionContactQueryService {

    private final RetentionContactMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public RetentionContactListResultDto getContactList(RetentionContactListRequestDto req) {
        return buildContactListResponse(req);
    }

    @Override
    @Transactional(readOnly = true)
    public RetentionContactListResultDto getMyRequestedContactList(Long empId, RetentionContactListRequestDto req) {
        req.setManagerId(empId); // 로그인한 상급자 ID 고정
        return buildContactListResponse(req);
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
        RetentionContactDetailDto detail = mapper.findContactDetailById(retentionId);

        if (detail == null) {
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND);
        }

        // 플래그 계산
        boolean deletable = detail.getResponse() == null;
        boolean feedbackWritable = detail.getResponse() != null && detail.getFeedback() == null;

        return RetentionContactDetailDto.builder()
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
    }

}
