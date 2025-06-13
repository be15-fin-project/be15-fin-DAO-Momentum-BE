package com.dao.momentum.organization.position.command.domain.aggregate;

import jakarta.persistence.*;
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
    
    private String name;

    private Integer level;

    @Enumerated(EnumType.STRING)
    private IsDeleted isDeleted;
}
