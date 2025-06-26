package com.dao.momentum.organization.employee.query.dto.response;

import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.aggregate.UserRoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@Schema(description = "사원 정보 DTO")
public class EmployeeSummaryDTO {
    @Schema(description = "사원 ID", example = "1")
    private long empId;

    @Schema(description = "사번", example = "20250001")
    private String empNo;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "부서 ID", example = "11")
    private Integer deptId;

    @Schema(description = "부서 이름", example = "마케팅팀")
    private String deptName;

    @Schema(description = "직위 ID", example = "8")
    private Integer positionId;

    @Schema(description = "직위 이름", example = "사원")
    private String positionName;

    @Schema(description = "권한 문자열", example = "MANAGER")
    private String userRolesInString;

    @Schema(description = "입사일")
    private LocalDate joinDate;

    @Schema(description = "재직 상태")
    private Status status;

    @Schema(description = "권한 목록")
    public List<UserRoleName> getUserRoles() {
        if (userRolesInString == null) {
            return List.of(UserRoleName.EMPLOYEE);
        }

        return Arrays.stream(userRolesInString.split(","))
                .map(UserRoleName::fromString)
                .toList();
    }
}
