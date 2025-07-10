package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.Work;
import com.dao.momentum.work.command.domain.aggregate.WorkTypeName;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkRepository {
    Work save(Work work);

    @Query("""
            SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END
            FROM Work w
            JOIN WorkType wt on w.typeId = wt.typeId
            WHERE w.empId = :empId
            AND FUNCTION('DATE', w.startAt) = :date
            AND wt.typeName = :typeName
            """)
    boolean existsByEmpIdAndStartAtDateAndTypeName(
            @Param("empId") long empId, @Param("date") LocalDate date, @Param("typeName") WorkTypeName typeName
    );

    @Query("""
            SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END
            FROM Work w
            JOIN WorkType wt on w.typeId = wt.typeId
            WHERE w.empId = :empId
            AND FUNCTION('DATE', w.startAt) = :date
            AND wt.typeName IN :typeNames
            """)
    boolean existsByEmpIdAndStartAtDateAndTypeNames(
            @Param("empId") long empId, @Param("date") LocalDate date, @Param("typeName") List<WorkTypeName> typeNames
    );

    @Query("""
            SELECT w
            FROM Work w
            JOIN WorkType wt ON w.typeId = wt.typeId
            WHERE w.empId = :empId
              AND FUNCTION('DATE', w.startAt) = :date
              AND wt.typeName IN :typeNames
            """)
    List<Work> findAllByEmpIdAndDateAndTypeNames(
            @Param("empId") long empId,
            @Param("date") LocalDate date,
            @Param("typeNames") List<WorkTypeName> typeNames
    );

    @Query("""
            SELECT w
            FROM Work w
            JOIN WorkType wt ON w.typeId = wt.typeId
            WHERE w.empId = :empId
              AND FUNCTION('DATE', w.startAt) = :date
              AND wt.typeName = :workType
            """)
    Optional<Work> findByEmpIdAndDateAndTypeName(
            long empId, LocalDate date, WorkTypeName workType);

    Optional<Work> findById(long workId);

    @Modifying
    @Query("""
    DELETE FROM Work w
    WHERE w.empId = :empId
      AND w.startAt >= :searchStartAt
      AND w.endAt < :searchEndAt
      AND w.typeId = :typeId
""")
    void deleteByEmployeeIdAndDateRangeAndWorkType(long empId, LocalDateTime searchStartAt, LocalDateTime searchEndAt, int typeId);

    @Query("""
                SELECT
                  CASE WHEN EXISTS (
                  SELECT 1
                    FROM Work w
                   WHERE w.empId = :empId
                  AND w.startAt >= :searchStartAt
                  AND w.endAt < :searchEndAt
                  AND w.typeId = :typeId
                  ) THEN TRUE ELSE FALSE END
            """)
    boolean existsByEmpIdAndDateRangeAndWorkType(long empId, LocalDateTime searchStartAt, LocalDateTime searchEndAt, int typeId);

    @Query("""
            SELECT w
            FROM Work w
            JOIN WorkType wt ON w.typeId = wt.typeId
            WHERE w.empId = :empId
              AND FUNCTION('DATE', w.startAt) >= :startDate
              AND FUNCTION('DATE', w.endAt) < :endDate
            """)
    List<Work> findAllByEmpIdAndDateAndTypeNames(
            @Param("empId") long empId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
