package com.dao.momentum.evaluation.eval.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "eval_round")
public class EvalRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_id", nullable = false, updatable = false)
    private Integer roundId;

    @Column(name = "round_no", nullable = false)
    private Integer roundNo;

    @Column(name = "start_at", nullable = false)
    private LocalDate startAt;

}
