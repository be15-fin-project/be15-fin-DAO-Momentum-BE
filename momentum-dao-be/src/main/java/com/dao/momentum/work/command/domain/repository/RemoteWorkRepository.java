package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.Overtime;
import com.dao.momentum.work.command.domain.aggregate.RemoteWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RemoteWorkRepository  extends JpaRepository<RemoteWork, Long> {

    @Query(value = "SELECT * FROM remote_work WHERE approve_id = :approveId", nativeQuery = true)
    Optional<RemoteWork> findByApproveId(Long approveId);

}
