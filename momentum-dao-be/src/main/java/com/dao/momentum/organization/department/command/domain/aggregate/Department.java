package com.dao.momentum.organization.department.command.domain.aggregate;

import com.dao.momentum.organization.department.command.domain.aggregate.IsDeleted;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deptId;

    private Integer parentDeptId;

    private String name;

    private String contact;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private IsDeleted isDeleted = IsDeleted.N;

}
