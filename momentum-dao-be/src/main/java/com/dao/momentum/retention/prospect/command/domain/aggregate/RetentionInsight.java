package com.dao.momentum.retention.prospect.command.domain.aggregate;

import com.dao.momentum.retention.prospect.command.application.dto.request.RetentionInsightDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "retention_insight")
public class RetentionInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insight_id", nullable = false, updatable = false)
    private Long insightId;

    @Column(name = "dept_id", nullable = false)
    private Integer deptId;

    @Column(name = "position_id", nullable = false)
    private Integer positionId;

    @Column(name = "round_id", nullable = false)
    private Integer roundId;

    @Column(name = "retention_score", nullable = false)
    private Integer retentionScore;

    @Column(name = "emp_count", nullable = false)
    private Integer empCount;

    @Column(name = "progress_20", nullable = false)
    private Integer progress20;

    @Column(name = "progress_40", nullable = false)
    private Integer progress40;

    @Column(name = "progress_60", nullable = false)
    private Integer progress60;

    @Column(name = "progress_80", nullable = false)
    private Integer progress80;

    @Column(name = "progress_100", nullable = false)
    private Integer progress100;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static RetentionInsight of(Integer roundId, RetentionInsightDto dto) {
        return new RetentionInsight(
                null,
                dto.deptId(),
                dto.positionId(),
                roundId,
                dto.retentionScore(),
                dto.empCount(),
                dto.progress20(),
                dto.progress40(),
                dto.progress60(),
                dto.progress80(),
                dto.progress100(),
                LocalDateTime.now()
        );
    }
}
