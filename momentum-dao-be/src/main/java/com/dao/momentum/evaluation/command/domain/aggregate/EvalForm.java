package com.dao.momentum.evaluation.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import com.dao.momentum.evaluation.command.domain.aggregate.UseStatus;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "eval_form")
public class EvalForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id", nullable = false, updatable = false)
    private Integer formId;

    @Column(name = "type_id", nullable = false)
    private Integer typeId;

    @Column(name = "title", length = 30, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_active", nullable = false, columnDefinition = "ENUM('Y','N') DEFAULT 'Y'")
    private UseStatus isActive;
}
