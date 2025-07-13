package com.dao.momentum.retention.prospect.command.domain.repository;

import com.dao.momentum.retention.prospect.command.domain.aggregate.RetentionSupport;
import com.dao.momentum.retention.prospect.command.infrastructure.repository.JpaRetentionSupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// 구현체
@Repository
@RequiredArgsConstructor
public class RetentionSupportRepositoryImpl implements RetentionSupportRepository {

    private final JpaRetentionSupportRepository jpaRepository;

    @Override
    public void saveAllSupports(List<RetentionSupport> supports) {
        jpaRepository.saveAll(supports);
    }

    @Override
    public Optional<RetentionSupport> findById(Long retentionId) {
        return jpaRepository.findById(retentionId);
    }
}
