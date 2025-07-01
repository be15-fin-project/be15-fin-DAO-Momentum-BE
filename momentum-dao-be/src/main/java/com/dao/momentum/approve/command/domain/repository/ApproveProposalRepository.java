package com.dao.momentum.approve.command.domain.repository;

import com.dao.momentum.approve.command.domain.aggregate.ApproveProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApproveProposalRepository extends JpaRepository<ApproveProposal, Long> {

    Optional<ApproveProposal> findByApproveId(Long approveId);
}

