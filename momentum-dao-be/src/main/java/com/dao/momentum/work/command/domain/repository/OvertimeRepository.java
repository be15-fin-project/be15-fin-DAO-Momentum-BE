package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OvertimeRepository extends JpaRepository<Overtime, Long> {

    @Query(value = "SELECT * FROM overtime WHERE approve_id = :approveId", nativeQuery = true)
    Optional<Overtime> findByApproveId(Long approveId);

}
