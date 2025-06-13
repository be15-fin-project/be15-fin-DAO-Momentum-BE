package com.dao.momentum.evaluation.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "eval_type")
public class EvalType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id", nullable = false, updatable = false)
    private Integer typeId;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;
}
