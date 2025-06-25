package com.dao.momentum.evaluation.hr.command.domain.aggregate;

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


    // === 생성 팩토리 메서드 ===
    public static HrObjection create(HrObjectionCreateDto dto, Integer defaultStatusId) {
        return HrObjection.builder()
                .resultId(dto.getResultId())
                .statusId(defaultStatusId)
                .reason(dto.getReason())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // === 응답 처리 메서드 (선택 기능용) ===
    public void respond(String responseMessage) {
        this.response = responseMessage;
        this.responseAt = LocalDateTime.now();
    }
}
