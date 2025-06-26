package com.dao.momentum.organization.contract.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @NotNull
    private Long empId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ContractType type;

    @Column(precision = 15, scale = 3)
    private BigDecimal salary;

    @CreatedDate
    private LocalDateTime createdAt;
}
