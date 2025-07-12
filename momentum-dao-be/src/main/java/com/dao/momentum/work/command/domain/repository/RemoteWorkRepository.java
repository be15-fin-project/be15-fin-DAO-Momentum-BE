package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.RemoteWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RemoteWorkRepository extends JpaRepository<RemoteWork, Long> {

    @Query(value = "SELECT * FROM remote_work WHERE approve_id = :approveId", nativeQuery = true)
    Optional<RemoteWork> findByApproveId(Long approveId);

    @Query(
            value = "SELECT * FROM remote_work WHERE start_at BETWEEN :startDate AND :endDate",
            nativeQuery = true
    )
    List<RemoteWork> findRemoteWorksBetween(LocalDate startDate, LocalDate endDate);

    @Modifying
    @Query("DELETE FROM RemoteWork rw WHERE rw.approveId = :approveId")
    void deleteRemoteWorkByApproveId(Long approveId);

}
