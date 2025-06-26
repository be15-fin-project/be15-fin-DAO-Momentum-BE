package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.BusinessTrip;
import com.dao.momentum.work.command.domain.aggregate.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessTripRepository extends JpaRepository<BusinessTrip, Long> {

    @Query(value = "SELECT * FROM business_trip WHERE approve_id = :approveId", nativeQuery = true)
    Optional<BusinessTrip> findByApproveId(Long approveId);

}
