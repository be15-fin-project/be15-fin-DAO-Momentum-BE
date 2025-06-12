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
    public Work(long empId, int typeId, LocalDateTime startAt, LocalDateTime endAt, int breakTime, LocalDateTime startPushedAt) {
        this.empId = empId;
        this.typeId = typeId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.breakTime = breakTime;
        this.startPushedAt = startPushedAt;
    }

    public Duration getWorkTime() {
        return Duration.between(this.startAt, this.endAt)
                .minusMinutes(this.breakTime);
    }

}
