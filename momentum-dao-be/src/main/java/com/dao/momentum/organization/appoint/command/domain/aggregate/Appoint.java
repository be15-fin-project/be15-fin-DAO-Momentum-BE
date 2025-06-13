package com.dao.momentum.organization.appoint.command.domain.aggregate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Appoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointId;

    private Long empId;

    private Integer beforePosition;

    private Integer afterPosition;

    private Integer beforeDepartment;

    private Integer afterDepartment;

    private AppointType type;

    private LocalDate appointDate;
}
