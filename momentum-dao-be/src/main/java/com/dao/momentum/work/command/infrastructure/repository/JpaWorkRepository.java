package com.dao.momentum.work.command.infrastructure.repository;

import com.dao.momentum.work.command.domain.aggregate.Work;
import com.dao.momentum.work.command.domain.repository.WorkRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaWorkRepository extends WorkRepository, JpaRepository<Work, Long> {
}
