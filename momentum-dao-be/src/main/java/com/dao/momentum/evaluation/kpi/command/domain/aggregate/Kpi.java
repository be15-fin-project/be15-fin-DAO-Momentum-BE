package com.dao.momentum.evaluation.kpi.command.domain.aggregate;

import com.dao.momentum.common.dto.Status;
import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
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

    @Column(name = "c_response", length = 255)
    private String cancelResponse;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_deleted", nullable = false, columnDefinition = "ENUM('Y','N') DEFAULT 'N'")
    private UseStatus isDeleted;


    public static Kpi applyCreateDTO(KpiCreateDTO dto, Long empId) {
        return Kpi.builder()
                .empId(empId)
                .statusId(Status.PENDING.getId())
                .goal(dto.goal())
                .goalValue(dto.goalValue())
                .kpiProgress(dto.kpiProgress())
                .progress25(dto.progress25())
                .progress50(dto.progress50())
                .progress75(dto.progress75())
                .progress100(dto.progress100())
                .deadline(dto.deadline())
                .createdAt(LocalDateTime.now())
                .isDeleted(UseStatus.N)
                .build();
    }

    public void cancel(String reason) {
        this.statusId = Status.PENDING.getId(); // 승인 대기로 전환
        this.isDeleted = UseStatus.Y;           // 삭제 요청 상태 표시
        this.cancelReason = reason;
    }

    public void approve(String reason) {
        this.statusId = Status.ACCEPTED.getId();
        this.reason = reason;
    }

    public void reject(String reason) {
        this.statusId = Status.REJECTED.getId();
        this.reason = reason;
    }

    public void approveCancel(String reason) {
        this.statusId = Status.ACCEPTED.getId();  // 취소 승인 → 상태도 삭제
        this.cancelResponse = reason;
        this.isDeleted = UseStatus.Y;
    }

    public void rejectCancel(String reason) {
        this.statusId = Status.ACCEPTED.getId(); // 취소 반려 → 반려 상태로 복구
        this.cancelResponse = reason;
        this.isDeleted = UseStatus.N;
    }

    public void updateProgress(Integer progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("진척도는 0 이상 100 이하 값만 가능합니다.");
        }
        this.kpiProgress = progress;
    }


}
