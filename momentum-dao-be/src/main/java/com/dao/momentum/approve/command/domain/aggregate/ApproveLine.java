package com.dao.momentum.approve.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "approve_line")
public class ApproveLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approve_line_id")
    private Long id;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @Column(name = "approve_id", nullable = false)
    private Long approveId;

    @Column(name = "approve_line_order", nullable = false)
    private Integer approveLineOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_required_all", nullable = false)
    private IsRequiredAll isRequiredAll;

    @Column(name = "complete_at")
    private LocalDateTime completeAt;

    @Builder
    public ApproveLine(Long approveId, Integer approveLineOrder,
                       IsRequiredAll isRequiredAll, LocalDateTime completeAt) {
        this.statusId = 1;
        this.approveId = approveId;
        this.approveLineOrder = approveLineOrder;
        this.isRequiredAll = isRequiredAll;
        this.completeAt = completeAt;
    }
}
