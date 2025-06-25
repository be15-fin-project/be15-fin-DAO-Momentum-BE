package com.dao.momentum.retention.interview.command.application.service;

import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactCreateDto;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactDeleteDto;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactFeedbackUpdateDto;
import com.dao.momentum.retention.interview.command.application.dto.request.RetentionContactResponseUpdateDto;
import com.dao.momentum.retention.interview.command.application.dto.response.RetentionContactDeleteResponse;
import com.dao.momentum.retention.interview.command.application.dto.response.RetentionContactFeedbackUpdateResponse;
import com.dao.momentum.retention.interview.command.application.dto.response.RetentionContactResponse;
import com.dao.momentum.retention.interview.command.application.dto.response.RetentionContactResponseUpdateResponse;

public interface RetentionContactCommandService {
    RetentionContactResponse createContact(RetentionContactCreateDto dto);

    RetentionContactDeleteResponse deleteContact(RetentionContactDeleteDto dto);

    RetentionContactResponseUpdateResponse reportResponse(RetentionContactResponseUpdateDto dto);

    RetentionContactFeedbackUpdateResponse giveFeedback(RetentionContactFeedbackUpdateDto dto);
}