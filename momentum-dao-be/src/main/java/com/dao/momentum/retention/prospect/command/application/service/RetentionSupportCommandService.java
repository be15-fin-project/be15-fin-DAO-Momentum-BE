package com.dao.momentum.retention.prospect.command.application.service;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;

import java.util.List;

public interface RetentionSupportCommandService {
    void saveAll(List<RetentionSupport> supports);
}
