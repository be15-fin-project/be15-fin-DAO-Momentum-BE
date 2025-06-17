package com.dao.momentum.organization.position.command.domain.repository;

import com.dao.momentum.organization.position.command.domain.aggregate.Position;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface PositionRepository {
    boolean existsByName(@NotBlank String name);

    Position save(Position position);

    boolean existsByLevel(Integer requestedLevel);

    void incrementLevelsGreaterThanEqual(Integer requestedLevel);

    void decrementLevelsGreater(Integer level);

    Optional<Position> findByPositionId( Integer positionId);

    Integer findMaxLevel();

    void incrementLevelsInRange(int startLevel, int endLevel);

    void decrementLevelsInRange(int startLevel, int endLevel);

    void deleteByPositionId(Integer positionId);
}
