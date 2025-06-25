package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "work")
@Getter
@NoArgsConstructor
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long workId;

    private long empId;

    private int typeId;

    private Integer vacationTypeId;

    @NotNull
    private LocalDateTime startAt;
    @NotNull
    private LocalDateTime endAt;

    private int breakTime; // 휴게시간 (분 단위)

    private LocalDateTime startPushedAt;

    private LocalDateTime endPushedAt;

    @Setter
    @Enumerated(EnumType.STRING)
    private IsNormalWork isNormalWork;

    @Builder
    public Work(long empId, int typeId, LocalDateTime startAt, LocalDateTime endAt, int breakTime, LocalDateTime startPushedAt, Integer vacationTypeId) {
        this.empId = empId;
        this.typeId = typeId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.breakTime = breakTime;
        this.startPushedAt = startPushedAt;
        this.vacationTypeId = vacationTypeId;
    }

    public void fromUpdate(LocalDateTime endAt, LocalDateTime endPushedAt, int breakTime) {
        this.endAt = endAt;
        this.endPushedAt = endPushedAt;
        this.breakTime = breakTime;
    }

    public void fromCorrection(LocalDateTime afterStartAt, LocalDateTime afterEndAt, int breakTime, Integer vacationTypeId) {
        this.startAt = afterStartAt;
        this.endAt = afterEndAt;
        this.breakTime = breakTime;
        this.vacationTypeId = vacationTypeId;
    }

    public Duration getWorkTime() {
        return Duration.between(this.startAt, this.endAt)
                .minusMinutes(this.breakTime);
    }

    public boolean isNormalWork(long requiredMinutes) {
        return this.getWorkTime().toMinutes() >= requiredMinutes;
    }

}
