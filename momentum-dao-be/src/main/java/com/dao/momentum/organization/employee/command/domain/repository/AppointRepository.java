package com.dao.momentum.organization.employee.command.domain.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.Appoint;
import com.dao.momentum.organization.employee.command.domain.aggregate.AppointType;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AppointRepository {

    Appoint save(Appoint appoint);

    List<Appoint> findByAppointDateLessThanEqual(LocalDate today);

    @Query(
            """
    SELECT COUNT(*) FROM Appoint a
    WHERE a.empId = :empId
    AND a.appointDate >= :startDate
    AND a.appointDate <= :endDate
    AND a.type = :type
"""
    )
    long countAppointsByEmpIdAndRangeOfDateAndType(long empId, LocalDate startDate, LocalDate endDate, AppointType type);

    @Query(
            """
    SELECT a
    FROM Appoint a
    WHERE a.empId = :empId
    AND a.appointDate >= :startDate
    AND a.appointDate <= :endDate
    AND a.type = :type
"""
    )
    List<Appoint> findAllAppointsByEmpIdAndRangeOfDateAndType(long empId, LocalDate startDate, LocalDate endDate, AppointType type);

    @Query("""
    SELECT a FROM Appoint a
    WHERE a.empId = :empId
      AND a.appointDate > :today
      AND (a.afterPosition != :currentPosition OR a.afterDepartment != :currentDepartment)
""")
    List<Appoint> findAllPendingAppoints(@Param("empId") long empId,
                                         @Param("today") LocalDate today,
                                         @Param("currentPosition") int currentPosition,
                                         @Param("currentDepartment") Integer currentDepartment);
}
