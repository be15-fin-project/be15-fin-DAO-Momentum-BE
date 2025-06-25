package com.dao.momentum.retention.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import com.dao.momentum.common.dto.UseStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "retention_contact")
public class RetentionContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "retention_id", nullable = false, updatable = false)
    private Long retentionId;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

    @Column(name = "response_at")
    private LocalDateTime responseAt;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_deleted", nullable = false)
    private UseStatus isDeleted;


    // 정적 생성 메서드
    public static RetentionContact create(Long targetId, Long managerId, Long writerId, String reason) {
        return RetentionContact.builder()
                .targetId(targetId)
                .managerId(managerId)
                .writerId(writerId)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .isDeleted(UseStatus.N)
                .build();
    }

    // Soft delete 처리
    public void markAsDeleted() {
        this.isDeleted = UseStatus.Y;
    }

    // 대응 정보 저장
    public void respond(String response, LocalDateTime responseAt) {
        this.response = response;
        this.responseAt = responseAt;
    }

    /**
     * 인사 피드백 저장
     */
    public void giveFeedback(String feedback) {
        this.feedback = feedback;
    }
}
