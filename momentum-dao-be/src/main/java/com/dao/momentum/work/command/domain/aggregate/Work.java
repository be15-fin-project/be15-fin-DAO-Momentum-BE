package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    private IsNormalWork isNormalWork;

}
