package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_correction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkCorrection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long workCorrectionId;

    private long workId;

    private long approveId;

    @NotNull
    private LocalDateTime beforeStartAt;

    @NotNull
    private LocalDateTime beforeEndAt;

    @NotNull
    private LocalDateTime afterStartAt;

    @NotNull
    private LocalDateTime afterEndAt;

    @NotBlank
    private String reason;

    @Builder
    public WorkCorrection(
            Long workId, Long approveId, LocalDateTime beforeStartAt, LocalDateTime beforeEndAt,
            LocalDateTime afterStartAt, LocalDateTime afterEndAt, String reason
    ) {
        this.workId = workId;
        this.approveId = approveId;
        this.beforeStartAt = beforeStartAt;
        this.beforeEndAt = beforeEndAt;
        this.afterStartAt = afterStartAt;
        this.afterEndAt = afterEndAt;
        this.reason = reason;
    }

}
