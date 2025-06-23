package com.dao.momentum.retention.query.service;

import com.dao.momentum.retention.query.dto.request.RetentionContactListRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionContactListResultDto;

public interface RetentionContactQueryService {

    RetentionContactListResultDto getContactList(RetentionContactListRequestDto req);

    RetentionContactListResultDto getMyRequestedContactList(Long empId, RetentionContactListRequestDto req);
}
