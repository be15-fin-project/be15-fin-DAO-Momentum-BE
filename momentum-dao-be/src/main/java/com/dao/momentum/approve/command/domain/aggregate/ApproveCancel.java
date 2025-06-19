package com.dao.momentum.approve.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "approve_cancel")
public class ApproveCancel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approve_cancel_id")
    private Long id;

    @Column(name = "approve_id", nullable = false)
    private Long approveId;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Builder
    public ApproveCancel(Long approveId, String cancelReason) {
        this.approveId = approveId;
        this.cancelReason = cancelReason;
    }

}
