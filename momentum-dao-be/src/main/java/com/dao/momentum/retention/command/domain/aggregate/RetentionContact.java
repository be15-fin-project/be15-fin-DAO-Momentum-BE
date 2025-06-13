package com.dao.momentum.retention.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import com.dao.momentum.evaluation.command.domain.aggregate.UseStatus;

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
    @Column(name = "is_deleted", nullable = false, columnDefinition = "ENUM('Y','N') DEFAULT 'N'")
    private UseStatus isDeleted;
}
