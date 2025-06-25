package com.dao.momentum.evaluation.hr.command.domain.aggregate;

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

    @Column(name = "perform_wt", nullable = false)
    private Integer performWt;

    @Column(name = "team_wt", nullable = false)
    private Integer teamWt;

    @Column(name = "attitude_wt", nullable = false)
    private Integer attitudeWt;

    @Column(name = "growth_wt", nullable = false)
    private Integer growthWt;

    @Column(name = "engagement_wt", nullable = false)
    private Integer engagementWt;

    @Column(name = "result_wt", nullable = false)
    private Integer resultWt;


    public void update(int performWt, int teamWt, int attitudeWt, int growthWt, int engagementWt, int resultWt) {
        this.performWt = performWt;
        this.teamWt = teamWt;
        this.attitudeWt = attitudeWt;
        this.growthWt = growthWt;
        this.engagementWt = engagementWt;
        this.resultWt = resultWt;
    }
}