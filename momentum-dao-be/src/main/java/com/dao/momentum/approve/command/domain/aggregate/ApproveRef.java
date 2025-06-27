package com.dao.momentum.approve.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "approve_ref")
public class ApproveRef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approve_ref_id")
    private Long id;

    @Column(name = "approve_id", nullable = false)
    private Long approveId;

    @Column(name = "emp_id", nullable = false)
    private Long empId;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_confirmed", nullable = false)
    private IsConfirmed isConfirmed;

    @Builder
    public ApproveRef(Long approveId, Long empId, IsConfirmed isConfirmed) {
        this.approveId = approveId;
        this.empId = empId;
        this.isConfirmed = isConfirmed;
    }

    /* 참조 상태를 '확인'으로 변경하는 메소드 */
    public void updateRefStatus() {
        isConfirmed = IsConfirmed.Y;
    }
}
