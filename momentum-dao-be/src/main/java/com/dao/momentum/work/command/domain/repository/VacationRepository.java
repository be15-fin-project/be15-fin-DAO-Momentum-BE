package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, Long> {

    @Query(value = "SELECT * FROM vacation WHERE approve_id = :approveId", nativeQuery = true)
    Optional<Vacation> findByApproveId(Long approveId);

    @Modifying
    @Query("DELETE FROM Vacation v WHERE v.approveId = :approveId")
    void deleteVacationByApproveId(Long approveId);

}
