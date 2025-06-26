package com.dao.momentum.retention.prospect.command.application.service;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionSupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetentionSupportCommandServiceImpl implements RetentionSupportCommandService {

    private final RetentionSupportRepository supportRepository;

    @Override
    public void saveAll(List<RetentionSupport> supports) {
        supportRepository.saveAllSupports(supports);
    }
}
