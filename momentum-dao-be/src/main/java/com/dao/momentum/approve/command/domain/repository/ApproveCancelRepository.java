package com.dao.momentum.approve.command.domain.repository;

import com.dao.momentum.approve.command.domain.aggregate.ApproveCancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApproveCancelRepository extends JpaRepository<ApproveCancel, Long> {

    Optional<ApproveCancel> findByApproveId(Long approveId);

    @Modifying
    @Query("DELETE FROM ApproveCancel ac WHERE ac.approveId = :approveId")
    void deleteApproveCancelByApproveId(Long approveId);

}

