package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.Vacation;
import com.dao.momentum.work.command.domain.aggregate.WorkCorrection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkCorrectionRepository extends JpaRepository<WorkCorrection, Long> {

    @Query(value = "SELECT * FROM work_correction WHERE approve_id = :approveId", nativeQuery = true)
    Optional<WorkCorrection> findByApproveId(Long approveId);

    @Modifying
    @Query("DELETE FROM WorkCorrection wc WHERE wc.approveId = :approveId")
    void deleteWorkCorrectionByApproveId(Long approveId);

}
