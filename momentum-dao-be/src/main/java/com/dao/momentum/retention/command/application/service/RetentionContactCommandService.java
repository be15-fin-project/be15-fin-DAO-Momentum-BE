package com.dao.momentum.retention.command.application.service;

import com.dao.momentum.retention.command.application.dto.request.RetentionContactCreateDto;
import com.dao.momentum.retention.command.application.dto.response.RetentionContactResponse;

public interface RetentionContactCommandService {
    RetentionContactResponse createContact(RetentionContactCreateDto dto);
}
