package com.dao.momentum.organization.employee.command.domain.aggregate;

import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeInfoUpdateRequest;
import com.dao.momentum.organization.employee.command.application.dto.request.MyInfoUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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

    public void fromUpdateEmpInfo(EmployeeInfoUpdateRequest request) {
        String newEmpNo = request.getEmpNo();
        String newEmail = request.getEmail();
        Status newStatus = request.getStatus();

        if (!this.empNo.equals(newEmpNo)) {
            this.empNo = newEmpNo;
        }
        if (!this.email.equals(newEmail)) {
            this.email = newEmail;
        }
        if (this.status != newStatus) {
            this.status = newStatus;
        }
    }

    public void fromAppoint(Integer afterDeptId, int afterPositionId) {
        if (!Objects.equals(this.deptId, afterDeptId)) {
            this.deptId = afterDeptId;
        }
        if (this.positionId != afterPositionId) {
            this.positionId = afterPositionId;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void updateVacations(int remainingDayoffHours, int remainingRefreshDays) {
        this.remainingDayoffHours = remainingDayoffHours;
        this.remainingRefreshDays = remainingRefreshDays;
        this.updatedAt = LocalDateTime.now();
    }

    /*
    * 연차 시간을 수정하는 메소드
    * */
    public void updateRemainingDayOff(int remainingDayoffHours) {
        this.remainingDayoffHours = remainingDayoffHours;
        this.updatedAt = LocalDateTime.now();
    }

    /*
    * 리프레시 휴가 일수를 수정하는 메소드
    * */
    public void updateRemainingRefreshDay(int remainingRefreshDays) {
        this.remainingRefreshDays = remainingRefreshDays;
        this.updatedAt = LocalDateTime.now();
    }

    public void fromUpdateMyInfo(MyInfoUpdateRequest request) {
        String name = request.getName();
        Gender gender = request.getGender();
        String address = request.getAddress();
        String contact = request.getContact();

        if (!this.name.equals(name)) {
            this.name = name;
        }
        if (this.gender != gender) {
            this.gender = gender;
        }
        if (!this.address.equals(address)) {
            this.address = address;
        }
        if (!this.contact.equals(contact)) {
            this.contact = contact;
        }
        this.updatedAt = LocalDateTime.now();
    }
}
