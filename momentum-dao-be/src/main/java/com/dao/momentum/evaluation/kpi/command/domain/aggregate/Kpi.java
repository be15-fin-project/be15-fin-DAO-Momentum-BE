package com.dao.momentum.evaluation.kpi.command.domain.aggregate;

import com.dao.momentum.common.dto.UseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "kpi")
public class Kpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kpi_id", nullable = false, updatable = false)
    private Long kpiId;

    @Column(name = "emp_id", nullable = false)
    private Long empId;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @Column(name = "goal", length = 255, nullable = false)
    private String goal;

    @Column(name = "goal_value", nullable = false)
    private Integer goalValue;

    @Column(name = "kpi_progress", nullable = false)
    private Integer kpiProgress;

    @Column(name = "progress_25", length = 255, nullable = false)
    private String progress25;

    @Column(name = "progress_50", length = 255, nullable = false)
    private String progress50;

    @Column(name = "progress_75", length = 255, nullable = false)
    private String progress75;

    @Column(name = "progress_100", length = 255, nullable = false)
    private String progress100;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "c_reason", length = 255)
    private String cancelReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_deleted", nullable = false, columnDefinition = "ENUM('Y','N') DEFAULT 'N'")
    private UseStatus isDeleted;
}
