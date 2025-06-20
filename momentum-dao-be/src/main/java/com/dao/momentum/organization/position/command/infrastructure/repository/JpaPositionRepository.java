package com.dao.momentum.organization.position.command.infrastructure.repository;

import com.dao.momentum.organization.position.command.domain.aggregate.Position;
import com.dao.momentum.organization.position.command.domain.repository.PositionRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaPositionRepository extends PositionRepository, JpaRepository<Position, Integer> {
    @Override
    boolean existsByName(String name);

    @Override
    Position save(Position position);

    @Override
    boolean existsByLevel(Integer requestedLevel);

    @Modifying
    @Query("UPDATE Position p SET p.level = p.level + 1 WHERE p.level >= :level AND p.isDeleted = 'N'")
    void incrementLevelsGreaterThanEqual(@Param("level") Integer level);

    @Modifying
    @Query("UPDATE Position p SET p.level = p.level - 1 WHERE p.level > :level AND p.isDeleted = 'N'")
    void decrementLevelsGreater(@Param("level") Integer level);

    @Override
    Optional<Position> findByPositionId(Integer positionId);

    @Query("SELECT MAX(p.level) FROM Position p WHERE p.isDeleted = 'N'")
    Integer findMaxLevel();

    @Modifying
    @Query("UPDATE Position p SET p.level = p.level + 1 " +
            "WHERE p.level BETWEEN :startLevel AND :endLevel AND p.isDeleted = 'N'")
    void incrementLevelsInRange(@Param("startLevel") int startLevel, @Param("endLevel") int endLevel);

    @Modifying
    @Query("UPDATE Position p SET p.level = p.level - 1 " +
            "WHERE p.level BETWEEN :startLevel AND :endLevel AND p.isDeleted = 'N'")
    void decrementLevelsInRange(@Param("startLevel") int startLevel, @Param("endLevel") int endLevel);

    void deleteByPositionId(Integer positionId);
}
