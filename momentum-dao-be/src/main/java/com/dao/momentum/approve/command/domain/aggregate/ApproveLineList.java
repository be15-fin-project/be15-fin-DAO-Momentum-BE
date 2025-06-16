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
@Table(name = "approve_line_list")
public class ApproveLineList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approve_line_list_id")
    private Long id;

    @Column(name = "approve_line_id", nullable = false)
    private Long approveLineId;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @Column(name = "emp_id")
    private Long empId;

    @Column(name = "complete_at")
    private LocalDateTime completeAt;

    @Column(name = "reason")
    private String reason;

    @Builder
    public ApproveLineList(Long approveLineId, Integer statusId, Long empId,
                           LocalDateTime completeAt, String reason) {
        this.approveLineId = approveLineId;
        this.statusId = statusId;
        this.empId = empId;
        this.completeAt = completeAt;
        this.reason = reason;
    }
}

