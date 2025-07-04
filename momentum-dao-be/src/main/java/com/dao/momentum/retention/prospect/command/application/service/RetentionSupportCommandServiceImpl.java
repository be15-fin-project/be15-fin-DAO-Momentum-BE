package com.dao.momentum.retention.prospect.command.application.service;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.domain.repository.RetentionSupportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetentionSupportCommandServiceImpl implements RetentionSupportCommandService {

    private final RetentionSupportRepository supportRepository;

    @Override
    public void saveAll(List<RetentionSupport> supports) {
        log.info(">>> saveAll called - count={}", supports.size());
        supportRepository.saveAllSupports(supports);
        log.info("근속 지원 저장 완료 - savedCount={}", supports.size());
    }
}
