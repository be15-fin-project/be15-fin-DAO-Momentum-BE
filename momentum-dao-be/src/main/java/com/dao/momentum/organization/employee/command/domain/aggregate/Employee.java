package com.dao.momentum.organization.employee.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empId;

    private String empNo;

    private String email;

    private Integer deptId;

    private Integer positionId;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    private String contact;

    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private Integer remainingDayoffHours;

    private Integer remainingRefreshDays;

    private LocalDate birthDate;

    public void setPassword(String password){
        this.password = password;
    }

    public void setEmpNo(String nextEmpNo) {
        this.empNo = nextEmpNo;
    }
}
