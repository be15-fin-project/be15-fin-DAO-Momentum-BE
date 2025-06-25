package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.RemoteWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemoteWorkRepository  extends JpaRepository<RemoteWork, Long> {
}
