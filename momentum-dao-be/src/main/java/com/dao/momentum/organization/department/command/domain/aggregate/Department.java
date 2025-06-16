package com.dao.momentum.organization.department.command.domain.aggregate;

import com.dao.momentum.organization.department.command.domain.aggregate.IsDeleted;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Integer deptHeadId;
    private Integer parentDeptId;
    private String name;
    private String contact;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private IsDeleted isDeleted = IsDeleted.N;

}
