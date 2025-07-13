package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OvertimeRepository extends JpaRepository<Overtime, Long> {

    @Query(value = "SELECT * FROM overtime WHERE approve_id = :approveId", nativeQuery = true)
    Optional<Overtime> findByApproveId(Long approveId);

    @Modifying
    @Query("DELETE FROM Overtime o WHERE o.approveId = :approveId")
    void deleteOvertimeByApproveId(Long approveId);

    @Query(
            value = "SELECT * FROM overtime WHERE start_at BETWEEN :startDate AND :endDate",
            nativeQuery = true
    )
    List<Overtime> findOvertimesBetween(LocalDate startDate, LocalDate endDate);

}
