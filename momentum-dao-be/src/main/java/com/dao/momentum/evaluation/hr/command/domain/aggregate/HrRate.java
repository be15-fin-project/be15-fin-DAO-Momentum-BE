package com.dao.momentum.evaluation.hr.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hr_rate")
public class HrRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id", nullable = false, updatable = false)
    private Integer rateId;

    @Column(name = "round_id", nullable = false)
    private Integer roundId;

    @Column(name = "rate_s", nullable = false)
    private Integer rateS;

    @Column(name = "rate_a", nullable = false)
    private Integer rateA;

    @Column(name = "rate_b", nullable = false)
    private Integer rateB;

    @Column(name = "rate_c", nullable = false)
    private Integer rateC;

    @Column(name = "rate_d", nullable = false)
    private Integer rateD;
}
