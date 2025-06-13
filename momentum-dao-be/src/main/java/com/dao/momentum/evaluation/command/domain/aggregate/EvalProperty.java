package com.dao.momentum.evaluation.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "eval_property")
public class EvalProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id", nullable = false, updatable = false)
    private Integer propertyId;

    @Column(name = "form_id", nullable = false)
    private Integer formId;

    @Column(name = "name", length = 30, nullable = false)
    private String name;
}
