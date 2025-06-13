package com.dao.momentum.evaluation.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hr_weight")
public class HrWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weight_id", nullable = false, updatable = false)
    private Integer weight_id;

    @Column(name = "round_id", nullable = false)
    private Integer roundId;

    @Column(name = "kpi_wt", nullable = false)
    private Integer kpiWeight;

    @Column(name = "peer_wt", nullable = false)
    private Integer peerWeight;

    @Column(name = "rank_wt", nullable = false)
    private Integer rankWeight;

    @Column(name = "attend_wt", nullable = false)
    private Integer attendWeight;
}
