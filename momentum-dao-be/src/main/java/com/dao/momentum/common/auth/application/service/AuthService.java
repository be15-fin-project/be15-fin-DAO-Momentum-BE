package com.dao.momentum.common.auth.application.service;

import com.dao.momentum.common.auth.application.dto.request.LoginRequest;
import com.dao.momentum.common.auth.application.dto.response.LoginResponse;
import com.dao.momentum.common.auth.application.dto.response.TokenResponse;
import com.dao.momentum.common.auth.domain.aggregate.RefreshToken;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.jwt.JwtTokenProvider;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.organization.employee.query.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
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

    public void logout(String refreshToken) {
        if(refreshToken == null)
            return;
        // refresh token의 서명 및 만료 검증
        jwtTokenProvider.validateToken(refreshToken);
        String userId = jwtTokenProvider.getUsernameFromJWT(refreshToken);
        redisTemplate.delete(userId);
    }

    public TokenResponse refreshToken(String providedRefreshToken) {
        // 리프레시 토큰 유효성 검사, 저장 되어 있는 userId 추출
        jwtTokenProvider.validateToken(providedRefreshToken);
        String empId = jwtTokenProvider.getUsernameFromJWT(providedRefreshToken);

        // Redis에 저장된 리프레시 토큰 조회
        RefreshToken storedRefreshToken = redisTemplate.opsForValue().get(empId);
        if (storedRefreshToken == null) {
            throw new BadCredentialsException("해당 유저로 조회되는 리프레시 토큰 없음");
        }

        // 넘어온 리프레시 토큰과 Redis의 토큰 비교
        if (!storedRefreshToken.getToken().equals(providedRefreshToken)) {
            throw new BadCredentialsException("리프레시 토큰 일치하지 않음");
        }

        Employee employee = employeeRepository.findByEmpId(Long.parseLong(empId))
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND)
                );

        List<String> employeeRoles = userRoleMapper.findByEmpId(employee.getEmpId());
        String[] employeeRoleArray = employeeRoles.toArray(new String[0]);

        String accessToken = jwtTokenProvider.createToken(String.valueOf(employee.getEmpId()), employeeRoleArray);
        String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(employee.getEmpId()), employeeRoleArray);


        RefreshToken newToken = RefreshToken.builder()
                .token(refreshToken)
                .build();

        // Redis에 새로운 리프레시 토큰 저장 (기존 토큰 덮어쓰기)
        redisTemplate.opsForValue().set(
                String.valueOf(employee.getEmpId()),
                newToken,
                Duration.ofDays(7)
        );

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
