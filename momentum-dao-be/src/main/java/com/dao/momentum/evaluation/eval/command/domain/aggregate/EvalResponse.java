package com.dao.momentum.evaluation.eval.command.domain.aggregate;

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

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateScore(int score) {
        this.score = score;
    }

    public void updateReason(String reason) {
        this.reason = reason;
    }

    public void updateScoreAndReason(int score, String reason) {
        this.score = score;
        this.reason = reason;
    }
}
