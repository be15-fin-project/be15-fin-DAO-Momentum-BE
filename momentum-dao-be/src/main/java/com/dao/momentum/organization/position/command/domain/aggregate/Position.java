package com.dao.momentum.organization.position.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer positionId;

    @NotNull
    private String name;

    @NotNull
    private Integer level;

    @Enumerated(EnumType.STRING)
    private IsDeleted isDeleted;
}
