package com.dao.momentum.common.auth.application.service;

import com.dao.momentum.common.auth.application.dto.request.LoginRequest;
import com.dao.momentum.common.auth.application.dto.response.LoginResponse;
import com.dao.momentum.common.auth.domain.aggregate.RefreshToken;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.jwt.JwtTokenProvider;
import com.dao.momentum.organization.employee.command.domain.aggregate.employee.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.organization.employee.query.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final EmployeeRepository employeeRepository;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, RefreshToken> redisTemplate;

    public LoginResponse login(LoginRequest loginRequest) {
        Employee employee = employeeRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND)
        );

        if(!passwordEncoder.matches(loginRequest.getPassword(), employee.getPassword())){
            throw new EmployeeException(ErrorCode.INVALID_CREDENTIALS);
        }

        List<String> employeeRoles = userRoleMapper.findByEmpId(employee.getEmpId());
        String[] employeeRoleArray = employeeRoles.toArray(new String[0]);

        String accessToken = jwtTokenProvider.createToken(String.valueOf(employee.getEmpId()), employeeRoleArray);
        String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(employee.getEmpId()), employeeRoleArray);

        RefreshToken redisRefreshToken = RefreshToken.builder()
                .token(refreshToken)
                .build();

        redisTemplate.opsForValue().set(
                String.valueOf(employee.getEmpId()),
                redisRefreshToken,
                Duration.ofDays(7)
        );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userRoles(employeeRoleArray)
                .build();
    }
}
