package com.dao.momentum.retention.command.domain.repository;

import com.dao.momentum.retention.command.domain.aggregate.RetentionContact;
import com.dao.momentum.retention.command.infrastructure.repository.JpaRetentionContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RetentionContactRepositoryImpl implements RetentionContactRepository {

    private final JpaRetentionContactRepository jpaRepository;

    @Override
    public RetentionContact save(RetentionContact contact) {
        return jpaRepository.save(contact);
    }

    @Override
    public Optional<RetentionContact> findById(Long id) {
        return jpaRepository.findById(id);
    }
}
