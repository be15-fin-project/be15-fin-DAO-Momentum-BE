package com.dao.momentum.evaluation.hr.command.domain.aggregate;

import com.dao.momentum.common.dto.UseStatus;
import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hr_objection")
public class HrObjection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "objection_id", nullable = false, updatable = false)
    private Long objectionId;

    @Column(name = "result_id", nullable = false)
    private Long resultId;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @Column(name = "reason", length = 255, nullable = false)
    private String reason;

    @Column(name = "response", length = 255)
    private String response;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "response_at")
    private LocalDateTime responseAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_deleted", nullable = false)
    private UseStatus isDeleted;

    // === 생성 팩토리 메서드 ===
    public static HrObjection create(HrObjectionCreateDto dto, Integer defaultStatusId) {
        return HrObjection.builder()
                .resultId(dto.getResultId())
                .statusId(defaultStatusId)
                .reason(dto.getReason())
                .createdAt(LocalDateTime.now())
                .isDeleted(UseStatus.N)
                .build();
    }

    public void markAsDeleted() {
        this.isDeleted = UseStatus.Y;
    }

    // 처리 승인 메서드
    public void approve(String reason) {
        this.statusId = 2; // 예: 2 = 승인
        this.response = reason;
        this.responseAt = LocalDateTime.now();
    }

    // 처리 반려 메서드
    public void reject(String rejectReason) {
        this.statusId = 3; // 예: 3 = 반려
        this.response = rejectReason;
        this.responseAt = LocalDateTime.now();
    }

}
