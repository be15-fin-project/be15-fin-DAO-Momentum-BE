package com.dao.momentum.retention.command.application.service;

import com.dao.momentum.retention.command.application.dto.request.RetentionContactCreateDto;
import com.dao.momentum.retention.command.application.dto.request.RetentionContactDeleteDto;
import com.dao.momentum.retention.command.application.dto.request.RetentionContactResponseUpdateDto;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactDeleteResponse;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactResponse;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactResponseUpdateResponse;

public interface RetentionContactCommandService {
    RetentionContactResponse createContact(RetentionContactCreateDto dto);

    RetentionContactDeleteResponse deleteContact(RetentionContactDeleteDto dto);

    RetentionContactResponseUpdateResponse reportResponse(RetentionContactResponseUpdateDto dto);
}