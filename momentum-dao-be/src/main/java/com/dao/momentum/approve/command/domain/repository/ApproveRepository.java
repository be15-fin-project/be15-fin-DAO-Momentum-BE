package com.dao.momentum.approve.command.domain.repository;

import com.dao.momentum.approve.command.domain.aggregate.Approve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApproveRepository extends JpaRepository<Approve, Long> {

    Optional<Approve> getApproveByApproveId(Long approveId);

}
