package com.dao.momentum.retention.query.service;

import com.dao.momentum.retention.query.dto.request.RetentionContactListRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionContactDetailDto;
import com.dao.momentum.retention.query.dto.response.RetentionContactListResultDto;

public interface RetentionContactQueryService {

    // 면담 요청 및 내역 조회
    RetentionContactListResultDto getContactList(RetentionContactListRequestDto req);

    RetentionContactListResultDto getMyRequestedContactList(Long empId, RetentionContactListRequestDto req);

    // 면담 상세 조회
    RetentionContactDetailDto getContactDetail(Long retentionId, Long requesterEmpId);

}
