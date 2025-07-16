package com.dao.momentum.organization.employee.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.email.service.EmailService;
import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeRegisterRequest;
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
import org.modelmapper.ModelMapper;
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
import java.util.*;
import java.util.stream.IntStream;

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
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    @Transactional
    public EmployeeCSVResponse createEmployees(MultipartFile file, UserDetails userDetails) {
        long adminId = Long.parseLong(userDetails.getUsername());
        employeeCommandService.validateActiveAdmin(adminId);

        // 1) 파싱 & 검증
        List<EmployeeRegisterRequest> requests = parseAndValidate(file);

        // 2) 저장 및 토큰 수집
        Map<Employee, String> emailMap = new LinkedHashMap<>();
        for (EmployeeRegisterRequest req : requests) {
            Employee emp = modelMapper.map(req, Employee.class);

            // 중복 이메일 체크
            if (employeeRepository.findByEmail(req.getEmail()).isPresent()) {
                throw new EmployeeException(ErrorCode.EMPLOYEE_ALREADY_EXISTS);
            }

            // 사번 생성 및 비밀번호 설정
            emp.setEmpNo(employeeCommandService.generateNextEmpNo());
            String rawPwd = employeeCommandService.generateRandomPassword();
            emp.setPassword(passwordEncoder.encode(rawPwd));

            employeeRepository.save(emp);
            // 저장 직후 토큰 생성
            String token = employeeCommandService.getPasswordResetToken(emp.getEmpId());
            emailMap.put(emp, token);
        }

        // 3) 이메일 사전 검증
        for (Employee emp : emailMap.keySet()) {
            if (!employeeRepository.existsByEmpId(emp.getEmpId())) {
                throw new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND);
            }
        }

        // 4) 일괄 이메일 발송
        for (Map.Entry<Employee, String> entry : emailMap.entrySet()) {
            emailService.sendPasswordResetEmail(entry.getKey(), entry.getValue());
        }

        // 5) 결과 응답
        List<Long> empIds = new ArrayList<>();
        for (Employee emp : emailMap.keySet()) {
            empIds.add(emp.getEmpId());
        }

        log.info("사원 CSV 등록 성공 - 요청자 ID: {}, 등록된 사원 ID: {}", adminId, empIds);
        return EmployeeCSVResponse.builder()
                .empIds(empIds)
                .message("사원 CSV 등록 성공")
                .build();
    }

    public List<EmployeeRegisterRequest> parseAndValidate(MultipartFile file) {
        List<String[]> rows = readAllRows(file);

        // 1) 헤더 정리 및 인덱스 맵 생성
        List<String> headers = normalizeHeader(rows.get(0));
        validateHeader(headers);
        Map<String, Integer> idxMap = buildIndexMap(headers);

        // 2) 각 행 처리
        List<EmployeeRegisterRequest> requests = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            String[] cols = rows.get(i);
            if (isEmptyRow(cols)) continue; // ",,,,," 행이 남아있으면 무시
            int line = i + 1;
            if (cols.length != headers.size()) {
                throw new EmployeeException(
                        ErrorCode.INVALID_COLUMN_COUNT, line, cols.length, headers.size());
            }
            validateRequiredFields(cols, idxMap, line);
            requests.add(toRequest(cols, idxMap));
        }
        return requests;
    }

    private List<String> normalizeHeader(String[] header) {
        return Arrays.stream(header)
                .map(h -> h == null ? "" : h.replace("\uFEFF", "").trim()) // BOM 처리
                .toList();
    }

    private void validateHeader(List<String> headers) {
        List<String> expected = List.of(
                "이름", "이메일 주소", "부서명", "직위명",
                "성별", "주소", "연락처", "입사일",
                "재직 상태", "생년월일", "부여 연차 시간", "부여 리프레시 휴가 일수"
        );
        if (!headers.equals(expected)) {
            log.warn("헤더 정보 불일치 - input: {} expected: {}", headers, expected);
            throw new EmployeeException(ErrorCode.INVALID_CSV_HEADER);
        }
    }

    private Map<String, Integer> buildIndexMap(List<String> headers) {
        Map<String, Integer> map = new LinkedHashMap<>();
        IntStream.range(0, headers.size())
                .forEach(i -> map.put(headers.get(i), i));
        return map;
    }

    private boolean isEmptyRow(String[] cols) {
        return cols == null || Arrays.stream(cols).allMatch(c -> c == null || c.isBlank());
    }

    private void validateRequiredFields(String[] cols, Map<String, Integer> idxMap, int line) {
        List<String> required = List.of(
                "이름", "이메일 주소", "부서명", "직위명",
                "성별", "주소", "연락처", "생년월일"
        );
        for (String key : required) {
            String val = cols[idxMap.get(key)];
            if (val == null || val.isBlank()) {
                throw new EmployeeException(
                        ErrorCode.REQUIRED_VALUE_NOT_FOUND, line, key);
            }
        }
    }

    private EmployeeRegisterRequest toRequest(String[] cols, Map<String, Integer> idx) {
        LocalDate joinDate = Optional.ofNullable(cols[idx.get("입사일")])
                .filter(s -> !s.isBlank())
                .map(LocalDate::parse)
                .orElse(LocalDate.now());

        int joinYear = joinDate.getYear();
        int joinMonth = joinDate.getMonthValue();
        int targetYear = LocalDate.now().getYear();

        return EmployeeRegisterRequest.builder()
                .name(cols[idx.get("이름")])
                .email(cols[idx.get("이메일 주소")])
                .deptId(parseDeptId(cols[idx.get("부서명")] ))
                .positionId(parsePositionId(cols[idx.get("직위명")] ))
                .gender(Gender.valueOf(cols[idx.get("성별")] ))
                .address(cols[idx.get("주소")])
                .contact(cols[idx.get("연락처")])
                .joinDate(joinDate)
                .status(parseStatus(cols[idx.get("재직 상태")]))
                .birthDate(LocalDate.parse(cols[idx.get("생년월일")]))
                .remainingDayoffHours(parseIntOrDefault(
                        cols[idx.get("부여 연차 시간")],
                        vacationTimeCommandService.computeDayoffHours(joinYear, joinMonth, targetYear)
                ))
                .remainingRefreshDays(parseIntOrDefault(
                        cols[idx.get("부여 리프레시 휴가 일수")],
                        vacationTimeCommandService.computeRefreshDays(joinYear, targetYear)
                ))
                .build();
    }

    private Integer parseDeptId(String deptName) {
        if (deptName == null || deptName.isBlank()) return null;

        com.dao.momentum.organization.department.command.domain.aggregate.IsDeleted isActive = com.dao.momentum.organization.department.command.domain.aggregate.IsDeleted.N;

        Department dept = departmentRepository.findByNameAndIsDeleted(
                        deptName, isActive)
                .orElseThrow(() -> new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND));
        return dept.getDeptId();
    }

    private Integer parsePositionId(String positionName) {
        Position pos = positionRepository.findByNameAndIsDeleted(
                        positionName, IsDeleted.N)
                .orElseThrow(() -> new PositionException(ErrorCode.POSITION_NOT_FOUND));
        return pos.getPositionId();
    }

    private Status parseStatus(String input) {
        if (input == null || input.isBlank()) {
            return Status.EMPLOYED;
        }
        return switch (input) {
            case "재직" -> Status.EMPLOYED;
            case "휴직" -> Status.ON_LEAVE;
            case "퇴사" -> Status.RESIGNED;
            default -> throw new EmployeeException(ErrorCode.INVALID_STATUS);
        };
    }

    private int parseIntOrDefault(String val, int def) {
        if (val == null || val.isBlank()) return def;
        return Integer.parseInt(val);
    }

    private List<String[]> readAllRows(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVReader csv = new CSVReader(reader)) {
            List<String[]> rows = csv.readAll();
            if (rows.isEmpty()) {
                throw new EmployeeException(ErrorCode.EMPTY_DATA_PROVIDED);
            }
            return rows;
        } catch (IOException e) {
            throw new EmployeeException(ErrorCode.CSV_READ_FAILED, e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }
}
