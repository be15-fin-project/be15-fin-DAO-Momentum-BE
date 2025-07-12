package com.dao.momentum.approve.command.domain.repository;

import com.dao.momentum.approve.command.domain.aggregate.ApproveReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApproveReceiptRepository extends JpaRepository<ApproveReceipt, Long> {

    Optional<ApproveReceipt> findByApproveId(Long approveId);

    @Modifying
    @Query("DELETE FROM ApproveReceipt ar WHERE ar.approveId = :approveId")
    void deleteApproveReceiptByApprovalId(Long approveId);

}

