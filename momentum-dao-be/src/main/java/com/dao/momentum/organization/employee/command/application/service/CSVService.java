package com.dao.momentum.organization.employee.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.employee.command.application.dto.response.EmployeeCSVResponse;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.Gender;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.organization.position.command.domain.aggregate.IsDeleted;
import com.dao.momentum.organization.position.command.domain.aggregate.Position;
import com.dao.momentum.organization.position.command.domain.repository.PositionRepository;
import com.dao.momentum.organization.position.exception.PositionException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CSVService {

    private final EmployeeCommandService employeeCommandService;
    private final VacationTimeCommandService vacationTimeCommandService;
    private final PasswordEncoder passwordEncoder;

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;


    @Transactional
    public EmployeeCSVResponse createEmployees(MultipartFile file, UserDetails userDetails) {
        long adminId = Long.parseLong(userDetails.getUsername());
        employeeCommandService.validateActiveAdmin(adminId);

        // 1) 파싱 & 검증
        List<Employee> entities = parseAndValidate(file);

        // 2) 저장
        entities.forEach(
                entity -> {
                    if (entity.getEmpNo() == null || entity.getEmpNo().isBlank()) {
                        entity.setEmpNo(employeeCommandService.generateNextEmpNo());
                    }
                    employeeRepository.save(entity);
                });

        List<Long> empIds = entities.stream().map(Employee::getEmpId).toList();

        // 3) 응답 DTO 반환
        log.info("사원 CSV 등록 성공 - 요청자 ID: {}, 요청일시: {}, 등록된 사원 ID: {}", adminId, LocalDateTime.now(), empIds);
        return EmployeeCSVResponse.builder()
                .empIds(empIds)
                .message("사원 CSV 등록 성공")
                .build();
    }

    /**
     * CSV 파싱 → 검증 → Entity 리스트 반환
     */
    public List<Employee> parseAndValidate(MultipartFile file) {
        List<String[]> rows = readAllRows(file);

        String[] header = rows.get(0);
        validateHeader(header);

        List<Employee> entities = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            int line = i + 1;
            String[] cols = rows.get(i);

            if (isEmptyRow(cols)) continue; // ",,,,," 행이 남아있으면 무시

            validateRow(cols, header, line);

            entities.add(toEntity(cols));
        }
        return entities;
    }

    // BOM 처리
    private String normalize(String s) {
        return s == null
                ? null
                : s.replace("\uFEFF", "").trim();
    }

    private boolean isEmptyRow(String[] cols) {
        return cols == null || Arrays.stream(cols).allMatch(c -> c == null || c.isBlank());
    }

    // 1) 전체 행 읽기
    private List<String[]> readAllRows(MultipartFile file) {
        try (
                InputStream is = file.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                CSVReader csv = new CSVReader(reader)
        ) {
            List<String[]> rows = csv.readAll();
            if (rows.isEmpty()) {
                log.warn("입력할 데이터 없음");
                throw new EmployeeException(ErrorCode.EMPTY_DATA_PROVIDED);
            }
            return rows;
        } catch (IOException e) {
            log.warn("CSV 파일 읽기 실패");
            throw new EmployeeException(ErrorCode.CSV_READ_FAILED, e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    // 2) 헤더 검증
    private void validateHeader(String[] header) {
        String[] expected = {
                "사번", "이름", "이메일 주소", "부서명", "직위명",
                "성별", "주소", "연락처", "입사일", "상태",
                "생년월일", "잔여 연차 시간", "잔여 리프레시 휴가 일수"
        };

        String[] cleaned = Arrays.stream(header)
                .map(this::normalize)
                .toArray(String[]::new);

        if (!Arrays.equals(expected, cleaned)) {
            log.warn("헤더 정보 불일치 - input: {}, expected: {}", Arrays.toString(cleaned), Arrays.toString(expected));
            throw new EmployeeException(ErrorCode.INVALID_CSV_HEADER);
        }
    }

    // 3) 각 행 검증
    private void validateRow(String[] cols, String[] header, int line) {
        if (cols.length != header.length) {
            log.warn("열 개수 불일치 - provided: {}개, expected: {}개", cols.length, header.length);
            throw new EmployeeException(
                    ErrorCode.INVALID_COLUMN_COUNT,
                    line, cols.length, header.length
            );
        }
        for (int idx = 0; idx < cols.length; idx++) {
            // 사번, 부서명, 입사일, 상태, 잔여 연차 시간, 잔여 리프레시 휴가 일수
            Set<Integer> optionalFields = new HashSet<>(Arrays.asList(0, 3, 8, 9, 11, 12));

            if (optionalFields.contains(idx)) continue;
            if (cols[idx] == null || cols[idx].isBlank()) {
                throw new EmployeeException(
                        ErrorCode.REQUIRED_VALUE_NOT_FOUND,
                        line, header[idx]
                );
            }
        }
    }

    // 4) Entity 변환
    private Employee toEntity(String[] cols) {
        LocalDateTime now = LocalDateTime.now();

        String deptName = cols[3];
        Integer deptId = parseDeptId(deptName);

        String positionName = cols[4];
        int positionId = parsePositionId(positionName);

        LocalDate joinDate = provideDefaultJoinDate(cols[8]);

        Status status = parseStatus(cols[9]);

        int joinYear = joinDate.getYear();
        int joinMonth = joinDate.getMonthValue();
        int targetYear = now.getYear();

        int dayoffHours = computeDayoffHours(cols[11], joinYear, joinMonth, targetYear);
        int refreshDays = computeRefreshDays(cols[12], joinYear, targetYear);

        return Employee.builder()
                .empNo(cols[0])
                .name(cols[1])
                .email(cols[2])
                .password(
                        passwordEncoder.encode(
                                employeeCommandService.generateRandomPassword()
                        )
                )
                .deptId(deptId)
                .positionId(positionId)
                .gender(Gender.valueOf(cols[5]))
                .address(cols[6])
                .contact(cols[7])
                .joinDate(joinDate)
                .status(status)
                .birthDate(LocalDate.parse(cols[10]))
                .remainingDayoffHours(dayoffHours)
                .remainingRefreshDays(refreshDays)
                .createdAt(now)
                .build();
    }

    private Integer parseDeptId(String deptName) {
        Integer deptId = null;
        com.dao.momentum.organization.department.command.domain.aggregate.IsDeleted isActive
                = com.dao.momentum.organization.department.command.domain.aggregate.IsDeleted.N;

        if (deptName != null && !deptName.isBlank()) {
            Department dept = departmentRepository.findByNameAndIsDeleted(deptName, isActive)
                    .orElseThrow(() -> new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND));
            deptId = dept.getDeptId();
        }
        return deptId;
    }

    private int parsePositionId(String positionName) {
        Position position = positionRepository.findByNameAndIsDeleted(positionName, IsDeleted.N)
                .orElseThrow(() -> new PositionException(ErrorCode.POSITION_NOT_FOUND));
        return position.getPositionId();
    }

    private LocalDate provideDefaultJoinDate(String input) {
        LocalDate joinDate = LocalDate.now();
        if (input != null && !input.isBlank()) {
            joinDate = LocalDate.parse(input);
        }
        return joinDate;
    }

    private Status parseStatus(String input) {
        if (input == null) {
            return Status.EMPLOYED;
        }

        return switch (input) {
            case "", "재직" -> Status.EMPLOYED;
            case "휴직" -> Status.ON_LEAVE;
            case "퇴사" -> Status.RESIGNED;
            default -> throw new EmployeeException(ErrorCode.INVALID_STATUS);
        };
    }

    private int computeDayoffHours(String input, int joinYear, int joinMonth, int targetYear) {
        if (input == null || input.isBlank()) {
            return vacationTimeCommandService.computeDayoffHours(joinYear, joinMonth, targetYear);
        }
        return Integer.parseInt(input);
    }

    private int computeRefreshDays(String input, int joinYear, int targetYear) {
        if (input == null || input.isBlank()) {
            return vacationTimeCommandService.computeRefreshDays(joinYear, targetYear);
        }
        return Integer.parseInt(input);
    }

}
