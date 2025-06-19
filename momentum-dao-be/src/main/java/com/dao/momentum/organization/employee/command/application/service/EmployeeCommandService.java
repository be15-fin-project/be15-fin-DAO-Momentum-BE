package com.dao.momentum.organization.employee.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.jwt.JwtTokenProvider;
import com.dao.momentum.email.service.EmailService;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeRegisterRequest;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.EmployeeRoles;
import com.dao.momentum.organization.employee.command.domain.aggregate.UserRoleName;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRolesRepository;
import com.dao.momentum.organization.employee.command.domain.repository.UserRoleRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeCommandService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeRolesRepository employeeRolesRepository;
    private final UserRoleRepository userRoleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void createEmployee(EmployeeRegisterRequest request){
        List<Integer> userRoleIds;

        Employee employee = modelMapper.map(request, Employee.class);

        if(employeeRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmployeeException(ErrorCode.EMPLOYEE_ALREADY_EXISTS);
        }

        String nextEmpNo = generateNextEmpNo();
        employee.setEmpNo(nextEmpNo);
        String randomPassword = generateRandomPassword();
        log.info("password : {}",randomPassword);

        employee.setPassword(passwordEncoder.encode(randomPassword));
        employeeRepository.save(employee);

        try{
            userRoleIds = userRoleRepository.findIdsByUserRoleNames(Arrays.stream(request.getEmployeeRoles()).toList());
        }catch(Exception e){
            throw new EmployeeException(ErrorCode.INVALID_CREDENTIALS);
        }
        for(int userRoleId : userRoleIds){
            employeeRolesRepository.save(new EmployeeRoles(null, employee.getEmpId(), userRoleId));
        }

        String passwordResetToken = jwtTokenProvider.createPasswordResetToken(
                String.valueOf(employee.getEmpId())
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

}
