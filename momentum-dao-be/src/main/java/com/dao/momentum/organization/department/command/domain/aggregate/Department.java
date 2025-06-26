package com.dao.momentum.organization.department.command.domain.aggregate;

import com.dao.momentum.organization.department.command.application.dto.request.DepartmentUpdateRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deptId;

    private Integer parentDeptId;

    @NotBlank
    private String name;

    @NotBlank
    private String contact;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(updatable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private IsDeleted isDeleted = IsDeleted.N;

    public void update(DepartmentUpdateRequest request) {
        this.contact = request.getContact();
        this.name = request.getName();
        this.parentDeptId = request.getParentDeptId();
    }

    public void delete(){
        this.isDeleted = IsDeleted.Y;
    }

}
