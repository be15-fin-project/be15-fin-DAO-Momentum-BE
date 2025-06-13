package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_correction")
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

}
