package com.dao.momentum.evaluation.eval.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "eval_score")
public class EvalScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id", nullable = false, updatable = false)
    private Long scoreId;

    @Column(name = "property_id", nullable = false)
    private Integer propertyId;

    @Column(name = "result_id", nullable = false)
    private Long resultId;

    @Column(name = "score", nullable = false)
    private Integer score;


    public void updateScore(Integer score) {
        this.score = score;
    }
}
