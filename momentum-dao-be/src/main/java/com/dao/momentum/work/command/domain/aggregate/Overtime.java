package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "overtime")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Overtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long overtimeId;

    private long approveId;

    @NotNull
    private LocalDateTime startAt;

    private int breakTime;

    @NotNull
    private LocalDateTime endAt;

    @NotBlank
    private String reason;

    @Builder
    public Overtime(Long approveId, LocalDateTime startAt, LocalDateTime endAt, int breakTime,String reason) {
        this.approveId = approveId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.breakTime = breakTime;
        this.reason = reason;
    }

}
