package com.dao.momentum.organization.employee.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.file.command.application.dto.response.DownloadUrlResponse;
import com.dao.momentum.file.command.application.service.FileService;
import com.dao.momentum.organization.employee.command.application.dto.request.*;
import com.dao.momentum.organization.employee.command.application.dto.response.*;
import com.dao.momentum.organization.employee.command.application.service.AppointCommandService;
import com.dao.momentum.organization.employee.command.application.service.CSVService;
import com.dao.momentum.organization.employee.command.application.service.EmployeeCommandService;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/employees")
@Tag(name = "사원 관리", description = "사원 정보 등록, 수정, 삭제 API")
public class EmployeeCommandController {
    private static final String CSV_KEY = "csv/0b66fc14-61fa-4a80-a502-18714e2081c1/employees.csv";

    private final EmployeeCommandService employeeService;
    private final FileService fileService;
    private final CSVService csvService;
    private final AppointCommandService appointCommandService;

    @Operation(summary = "사원 등록", description = "관리자는 사원의 정보를 입력하여 사원을 등록할 수 있다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createEmployee(@RequestBody @Valid EmployeeRegisterRequest request){
        //관리자 권한 검사 로그인 기능 구현 이후 추후 추가
        employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    @Operation(summary = "사원 기본 정보 수정", description = "사원이 본인의 기본 정보를 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoUpdateResponse>> updateMyInfo(
            @RequestBody @Valid MyInfoUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        MyInfoUpdateResponse response = employeeService.updateMyInfo(request, userDetails);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "CSV 양식 다운로드", description = "관리자가 사원 CSV 등록에 필요한 CSV 양식 파일을 다운로드 합니다.")
    @GetMapping("/csv")
    public ResponseEntity<ApiResponse<DownloadUrlResponse>> downloadCSVFormat() {
        DownloadUrlResponse response = fileService.generateDownloadUrl(CSV_KEY);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "사원 CSV 등록", description = "관리자가 CSV 파일을 업로드하여 사원을 일괄 등록합니다.")
    @PostMapping("/csv")
    public ResponseEntity<ApiResponse<EmployeeCSVResponse>> createEmployees(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (file.isEmpty()) {
            throw new EmployeeException(ErrorCode.CSV_NOT_FOUND);
        }
        validateFileType(file);

        EmployeeCSVResponse response = csvService.createEmployees(file, userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    private void validateFileType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        log.info("Uploaded Content-Type: {}", file.getContentType());

        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new EmployeeException(ErrorCode.NOT_A_CSV);
        }
    }

    @Operation(summary = "사원 기본 정보 수정", description = "관리자가 사원의 사번, 재직 상태, 이메일을 수정합니다.")
    @PutMapping("/{empId}")
    public ResponseEntity<ApiResponse<EmployeeInfoUpdateResponse>> updateEmployeeInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long empId,
            @RequestBody @Valid EmployeeInfoUpdateRequest request){

        return ResponseEntity.ok(
                ApiResponse.success(
                        employeeService.updateEmployeeInfo(userDetails, empId, request)
                )
        );
    }

    @Operation(summary = "사원 인사 정보 수정", description = "관리자가 사원의 인사 정보를 수정합니다.")
    @PutMapping("/{empId}/hr-info")
    public ResponseEntity<ApiResponse<EmployeeRecordsUpdateResponse>> updateEmployeeRecords(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long empId,
            @RequestBody @Valid EmployeeRecordsUpdateRequest request){

        return ResponseEntity.ok(
                ApiResponse.success(
                        employeeService.updateEmployeeRecords(userDetails, empId, request)
                )
        );
    }

    @Operation(summary = "사원 발령 등록", description = "관리자가 사원의 발령 정보를 등록합니다.")
    @PostMapping("/appoints")
    public ResponseEntity<ApiResponse<AppointCreateResponse>> createAppoint(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AppointCreateRequest request){

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        appointCommandService.createAppoint(userDetails, request)
                )
        );
    }

    @Operation(summary = "사원 권한 수정", description = "관리자가 사원 권한을 수정합니다.")
    @PutMapping("/roles")
    public ResponseEntity<ApiResponse<RoleUpdateResponse>> updateRole(
            @RequestBody @Valid RoleUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        RoleUpdateResponse response = employeeService.updateRole(request, userDetails);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
