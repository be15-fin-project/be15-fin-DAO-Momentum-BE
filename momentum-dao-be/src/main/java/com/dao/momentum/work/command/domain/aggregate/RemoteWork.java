package com.dao.momentum.work.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "remote_work")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoteWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long remoteWorkId;

    private long approveId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String reason;

    @Builder
    public RemoteWork(Long approveId, LocalDate startDate, LocalDate endDate, String reason) {
        this.approveId = approveId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }
}
