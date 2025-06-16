package com.dao.momentum.organization.position.command.domain.repository;

import com.dao.momentum.organization.position.command.domain.aggregate.Position;
import jakarta.validation.constraints.NotBlank;

public interface PositionRepository {
    boolean existsByName(@NotBlank String name);

    Position save(Position position);

    boolean existsByLevel(Integer requestedLevel);

    void incrementLevelsGreaterThanEqual(Integer requestedLevel);
}
