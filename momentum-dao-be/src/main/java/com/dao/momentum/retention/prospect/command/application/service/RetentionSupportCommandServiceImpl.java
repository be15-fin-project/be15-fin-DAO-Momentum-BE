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
        log.info("API 호출 시작 - saveAll, 요청 파라미터: supportsCount={}", supports.size());

        // 데이터 저장 시작
        supportRepository.saveAllSupports(supports);

        // 저장 완료 후 로그
        log.info("API 호출 성공 - saveAll, 근속 지원 저장 완료 - savedCount={}", supports.size());
    }
}
