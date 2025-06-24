package com.dao.momentum.retention.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "retention_support")
public class RetentionSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "retention_id", nullable = false, updatable = false)
    private Long retentionId;

    @Column(name = "emp_id", nullable = false)
    private Long empId;

    @Column(name = "round_id", nullable = false)
    private Integer roundId;

    @Column(name = "job_level", nullable = false)
    private Integer jobLevel;

    @Column(name = "comp_level", nullable = false)
    private Integer compLevel;

    @Column(name = "relation_level", nullable = false)
    private Integer relationLevel;

    @Column(name = "growth_level", nullable = false)
    private Integer growthLevel;

    @Column(name = "tenure_level", nullable = false, precision = 4, scale = 2)
    private BigDecimal tenureLevel;

    @Column(name = "wlb_level", nullable = false)
    private Integer wlbLevel;

    @Column(name = "retention_score", nullable = false)
    private Integer retentionScore;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
