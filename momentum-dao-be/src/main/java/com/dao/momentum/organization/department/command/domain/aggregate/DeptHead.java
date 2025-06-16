package com.dao.momentum.organization.department.command.domain.aggregate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeptHead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deptHeadId;

    Long empId;

    @NotNull
    Integer deptId;
}
