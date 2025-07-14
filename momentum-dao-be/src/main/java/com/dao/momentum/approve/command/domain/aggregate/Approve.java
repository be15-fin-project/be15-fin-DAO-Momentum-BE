package com.dao.momentum.approve.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "approve")
public class Approve {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approve_id")
    private Long approveId;

    @Column(name = "parent_approve_id")
    private Long parentApproveId;

    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "emp_id")
    private Long empId;

    @Column(name = "approve_title")
    private String approveTitle;

    @Column(name = "approve_type")
    @Enumerated(EnumType.STRING)
    private ApproveType approveType;

    @Column(name = "create_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "complete_at")
    private LocalDateTime completeAt;

    @Column(name = "cancel_at")
    private LocalDateTime cancelAt;

    @Builder
    public Approve(
            Long parentApproveId, Long empId, String approveTitle,
            ApproveType approveType, LocalDateTime completeAt, LocalDateTime cancelAt
    ) {
        this.parentApproveId = parentApproveId;
        this.statusId = 1;
        this.empId = empId;
        this.approveTitle = approveTitle;
        this.approveType = approveType;
        this.createdAt = LocalDateTime.now();
        this.completeAt = completeAt;
        this.cancelAt = cancelAt;
    }

    public void updateApproveStatus(int statusId) {
        this.statusId = statusId;
        this.completeAt = LocalDateTime.now();
    }

    public void insertCancelDate() {
        this.cancelAt = LocalDateTime.now();
    }

    public void updateTitle(String approveTitle) {
        this.approveTitle = approveTitle;
    }

}
