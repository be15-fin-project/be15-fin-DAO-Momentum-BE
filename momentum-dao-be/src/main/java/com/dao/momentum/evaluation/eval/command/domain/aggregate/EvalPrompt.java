package com.dao.momentum.evaluation.eval.command.domain.aggregate;

import com.dao.momentum.common.dto.UseStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "eval_prompt")
public class EvalPrompt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id", nullable = false, updatable = false)
    private Integer promptId;

    @Column(name = "property_id", nullable = false)
    private Integer propertyId;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_positive", nullable = false, columnDefinition = "ENUM('Y','N') DEFAULT 'Y'")
    private UseStatus isPositive;
}
