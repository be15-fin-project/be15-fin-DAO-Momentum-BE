package com.dao.momentum.organization.position.command.infrastructure.repository;

import com.dao.momentum.organization.position.command.domain.aggregate.Position;
import com.dao.momentum.organization.position.command.domain.repository.PositionRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

}
