package com.dao.momentum.evaluation.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "eval_response")
public class EvalResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id", nullable = false, updatable = false)
    private Long resultId;

    @Column(name = "round_id", nullable = false)
    private Integer roundId;

    @Column(name = "form_id")
    private Integer formId;

    @Column(name = "eval_id", nullable = false)
    private Long evalId;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "property_score", columnDefinition = "TEXT", nullable = false)
    private String propertyScore;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
