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
        log.info("API 호출 시작 - getContactList, 요청 파라미터: page={}, size={}, managerId={}",
                req.page(), req.size(), req.managerId());

        RetentionContactListResultDto result = buildContactListResponse(req);

        log.info("API 호출 성공 - getContactList, 조회된 아이템 수: {}", result.items().size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public RetentionContactListResultDto getMyRequestedContactList(Long empId, RetentionContactListRequestDto req) {
        log.info("API 호출 시작 - getMyRequestedContactList, 요청 파라미터: empId={}, page={}, size={}",
                empId, req.page(), req.size());

        req = RetentionContactListRequestDto.builder().managerId(empId).build();
        RetentionContactListResultDto result = buildContactListResponse(req);

        log.info("API 호출 성공 - getMyRequestedContactList, 조회된 아이템 수: {}", result.items().size());
        return result;
    }

    private RetentionContactListResultDto buildContactListResponse(RetentionContactListRequestDto req) {
        log.info("Contact list query 시작 - page={}, size={}", req.page(), req.size());

        long total = mapper.countContacts(req);
        List<RetentionContactItemDto> items = mapper.findContacts(req);
        if (items == null) {
            log.error("면담 요청 내역 조회 실패 - 데이터 없음");
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND);
        }

        Pagination pagination = Pagination.builder()
                .currentPage(req.page())
                .totalItems(total)
                .totalPage((int) Math.ceil((double) total / req.size()))
                .build();

        log.info("Contact list 조회 성공 - 총 아이템 수: {}", total);
        return RetentionContactListResultDto.builder()
                .items(items)
                .pagination(pagination)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RetentionContactDetailDto getContactDetail(Long retentionId, Long requesterEmpId) {
        log.info("API 호출 시작 - getContactDetail, 요청 파라미터: retentionId={}, requesterEmpId={}", retentionId, requesterEmpId);

        RetentionContactDetailDto detail = mapper.findContactDetailById(retentionId);
        if (detail == null) {
            log.error("면담 요청 상세 조회 실패 - retentionId={} 데이터 없음", retentionId);
            throw new InterviewException(ErrorCode.RETENTION_CONTACT_NOT_FOUND);
        }

        boolean deletable = detail.response() == null;
        boolean feedbackWritable = detail.response() != null && detail.feedback() == null;

        RetentionContactDetailDto result = RetentionContactDetailDto.builder()
                .retentionId(detail.retentionId())
                .targetName(detail.targetName())
                .targetNo(detail.targetNo())
                .deptName(detail.deptName())
                .positionName(detail.positionName())
                .managerId(detail.managerId())
                .managerNo(detail.managerNo())
                .managerName(detail.managerName())
                .reason(detail.reason())
                .createdAt(detail.createdAt())
                .response(detail.response())
                .responseAt(detail.responseAt())
                .feedback(detail.feedback())
                .deletable(deletable)
                .feedbackWritable(feedbackWritable)
                .build();

        log.info("API 호출 성공 - getContactDetail, retentionId={}, deletable={}, feedbackWritable={}",
                result.retentionId(), result.deletable(), result.feedbackWritable());
        return result;
    }
}
