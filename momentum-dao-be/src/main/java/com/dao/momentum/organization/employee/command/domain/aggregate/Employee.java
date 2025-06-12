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
    private Integer emp_id;

    private String emp_no;

    private Integer dept_id;

    private Integer position_id;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    private String contact;

    private LocalDate join_date;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime create_at;

    private LocalDateTime updated_at;

    private Integer remaining_dayoff_hours;

    private Integer remaining_refresh_days;

    private LocalDate birth_date;
}
