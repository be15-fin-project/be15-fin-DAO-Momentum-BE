package com.dao.momentum.evaluation.command.domain.aggregate;

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
}
