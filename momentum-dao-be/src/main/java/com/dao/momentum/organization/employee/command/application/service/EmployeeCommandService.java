package com.dao.momentum.organization.employee.command.application.service;

import com.dao.momentum.common.auth.domain.aggregate.PasswordResetToken;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.jwt.JwtTokenProvider;
import com.dao.momentum.email.service.EmailService;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeInfoUpdateRequest;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeRecordsUpdateRequest;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeRegisterRequest;
import com.dao.momentum.organization.employee.command.application.dto.response.EmployeeInfoDTO;
import com.dao.momentum.organization.employee.command.application.dto.response.EmployeeInfoUpdateResponse;
import com.dao.momentum.organization.employee.command.application.dto.response.EmployeeRecordsUpdateResponse;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.EmployeeRecords;
import com.dao.momentum.organization.employee.command.domain.aggregate.EmployeeRoles;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRecordsRepository;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRolesRepository;
import com.dao.momentum.organization.employee.command.domain.repository.UserRoleRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeCommandService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeRolesRepository employeeRolesRepository;
    private final EmployeeRecordsRepository employeeRecordsRepository;
    private final UserRoleRepository userRoleRepository;

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, PasswordResetToken> passwordResetTokenRedisTemplate;

    @Transactional
    public void createEmployee(EmployeeRegisterRequest request) {
        List<Integer> userRoleIds;

        Employee employee = modelMapper.map(request, Employee.class);

        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmployeeException(ErrorCode.EMPLOYEE_ALREADY_EXISTS);
        }

        String nextEmpNo = generateNextEmpNo();
        employee.setEmpNo(nextEmpNo);
        String randomPassword = generateRandomPassword();
        log.info("password : {}", randomPassword);

        employee.setPassword(passwordEncoder.encode(randomPassword));
        employeeRepository.save(employee);

        try {
            userRoleIds = userRoleRepository.findIdsByUserRoleNames(Arrays.stream(request.getEmployeeRoles()).toList());
        } catch (Exception e) {
            throw new EmployeeException(ErrorCode.INVALID_CREDENTIALS);
        }
        for (int userRoleId : userRoleIds) {
            employeeRolesRepository.save(new EmployeeRoles(null, employee.getEmpId(), userRoleId));
        }

        String passwordResetToken = jwtTokenProvider.createPasswordResetToken(
                String.valueOf(employee.getEmpId())
        );

        PasswordResetToken redisPasswordResetToken = PasswordResetToken.builder()
                .token(passwordResetToken)
                .build();

        passwordResetTokenRedisTemplate.opsForValue().set(
                String.valueOf(employee.getEmpId()),
                redisPasswordResetToken,
                Duration.ofDays(1)
        );

        //이메일 처리
        emailService.sendPasswordResetEmail(employee,passwordResetToken);

    }

    public String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()-_=+";

        String allChars = upper + lower + digits + special;
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        // 최소 1개씩 보장
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // 나머지 5자 랜덤 추가
        for (int i = 0; i < 5; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 순서 섞기
        List<Character> pwChars = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwChars, random);

        return pwChars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public String generateNextEmpNo() {
        String maxEmpNo = employeeRepository.findMaxEmpNo(); // 예: "20240023"

        String currentYear = String.valueOf(LocalDate.now().getYear());

        if (maxEmpNo == null || maxEmpNo.length() != 8 || !maxEmpNo.startsWith(currentYear)) {
            // 올해 첫 사번
            return currentYear + "0001";
        }

        // 기존 사번에서 숫자 증가
        String lastSequence = maxEmpNo.substring(4); // 예: "0023"
        int nextSequence = Integer.parseInt(lastSequence) + 1;

        // 4자리 0채움 포맷
        String nextEmpNo = currentYear + String.format("%04d", nextSequence);
        return nextEmpNo;
    }

    @Transactional
    public EmployeeInfoUpdateResponse updateEmployeeInfo(UserDetails userDetails, long empId, EmployeeInfoUpdateRequest request) {
        long adminId = Long.parseLong(userDetails.getUsername());
        validateActiveAdmin(adminId);

        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        employee.fromUpdateEmpInfo(request);
        employeeRepository.save(employee);

        EmployeeInfoDTO employeeInfo = EmployeeInfoDTO.fromEmployee(employee);
        log.info("직원 정보 수정 완료 - 직원 ID: {}, 수정자(관리자) ID: {}, 수정 일시: {}", empId, adminId, LocalDateTime.now());

        return EmployeeInfoUpdateResponse.builder()
                .employeeInfo(employeeInfo)
                .message("직원 정보 수정 완료")
                .build();
    }

    @Transactional
    public EmployeeRecordsUpdateResponse updateEmployeeRecords(UserDetails userDetails, long empId, EmployeeRecordsUpdateRequest request) {
        long adminId = Long.parseLong(userDetails.getUsername());
        validateActiveAdmin(adminId);
        validateTargetEmployee(empId);

        // 1. 삭제 처리 및 검증
        List<Long> idsToDelete = request.getIdsToDelete();
        List<Long> deletedIds = deleteRequestedRecords(idsToDelete, empId);

        // 2. 삽입 처리
        List<EmployeeRecordsUpdateRequest.EmployeeRecordsItemRequest> itemsToInsert = request.getInsertItems();
        List<Long> insertedIds = insertEmployeeRecords(itemsToInsert, empId);

        log.info("직원 인사 정보 수정 완료 - 직원 ID: {}, 수정자(관리자) ID: {}, 수정 일시: {}", empId, adminId, LocalDateTime.now());

        return EmployeeRecordsUpdateResponse.builder()
                .insertedIds(insertedIds)
                .deletedIds(deletedIds)
                .message("직원 인사 정보 수정 완료")
                .build();
    }

    public void validateActiveAdmin(long adminId) {
        Employee admin = employeeRepository.findByEmpId(adminId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));
        Status adminStatus = admin.getStatus();

        // 관리자가 휴직 또는 퇴직 상태이면 수정 불가
        if (adminStatus != Status.EMPLOYED) {
            log.warn("권한 없는 사용자의 요청 - 요청자 ID: {}, 요청자 상태: {}", adminId, adminStatus);
            throw new EmployeeException(ErrorCode.NOT_EMPLOYED_USER);
        }
    }

    private void validateTargetEmployee(long empId) {
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }
    }

    private List<Long> deleteRequestedRecords(List<Long> idsToDelete, long empId) {
        if (idsToDelete == null || idsToDelete.isEmpty()) {
            return List.of();
        }

        List<EmployeeRecords> recordsToDelete = employeeRecordsRepository.findAllByRecordIdIn(idsToDelete);

        Set<Long> requestedIdSet = new HashSet<>(idsToDelete);
        Set<Long> foundIdSet = recordsToDelete.stream()
                .map(EmployeeRecords::getRecordId)
                .collect(Collectors.toSet());

        if (!requestedIdSet.equals(foundIdSet)) {
            log.warn("유효하지 않은 삭제 요청 - 삭제 요청 id: {}, 조회된 id: {}", requestedIdSet, foundIdSet);
            throw new EmployeeException(ErrorCode.INVALID_COMMAND_REQUEST);
        }

        // 삭제 요청된 empId가 record에 기록된 empId와 일치하는 지 검증
        recordsToDelete.forEach(rec -> {
            if (rec.getEmpId() != empId) {
                log.warn("유효하지 않은 삭제 요청 - rec.empId: {}, rec.recordId: {}, requestedEmpId: {}", rec.getEmpId(), rec.getRecordId(), empId);
                throw new EmployeeException(ErrorCode.INVALID_COMMAND_REQUEST);
            }
        });

        employeeRecordsRepository.deleteAllByRecordIdIn(idsToDelete);
        return idsToDelete;
    }

    private List<Long> insertEmployeeRecords(List<EmployeeRecordsUpdateRequest.EmployeeRecordsItemRequest> insertItems, long empId) {
        if (insertItems == null || insertItems.isEmpty()) return List.of();

        List<EmployeeRecords> recordsToInsert = insertItems.stream()
                .map(item ->
                        EmployeeRecords.builder()
                                .empId(empId)
                                .type(item.getType())
                                .organization(item.getOrganization())
                                .startDate(item.getStartDate())
                                .endDate(item.getEndDate())
                                .name(item.getName())
                                .build()
                ).toList();

        return recordsToInsert.stream()
                .map(employeeRecordsRepository::save)
                .map(EmployeeRecords::getRecordId)
                .toList();
    }


}
