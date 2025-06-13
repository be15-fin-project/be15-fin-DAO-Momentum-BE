package com.dao.momentum.retention.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "rating_score", nullable = false)
    private Integer ratingScore;

    @Column(name = "peer_score", nullable = false)
    private Integer peerScore;

    @Column(name = "performance_score", nullable = false)
    private Integer performanceScore;

    @Column(name = "attend_score", nullable = false)
    private Integer attendScore;

    @Column(name = "retention_score", nullable = false)
    private Integer retentionScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
