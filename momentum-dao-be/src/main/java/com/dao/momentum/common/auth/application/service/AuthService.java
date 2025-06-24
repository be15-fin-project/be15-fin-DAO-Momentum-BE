package com.dao.momentum.common.auth.application.service;

import com.dao.momentum.common.auth.application.dto.request.LoginRequest;
import com.dao.momentum.common.auth.application.dto.request.PasswordChangeRequest;
import com.dao.momentum.common.auth.application.dto.request.PasswordResetLinkRequest;
import com.dao.momentum.common.auth.application.dto.request.PasswordResetRequest;
import com.dao.momentum.common.auth.application.dto.response.LoginResponse;
import com.dao.momentum.common.auth.application.dto.response.PasswordChangeResponse;
import com.dao.momentum.common.auth.application.dto.response.PasswordResetResponse;
import com.dao.momentum.common.auth.application.dto.response.TokenResponse;
import com.dao.momentum.common.auth.domain.aggregate.PasswordResetToken;
import com.dao.momentum.common.auth.domain.aggregate.RefreshToken;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.jwt.JwtTokenProvider;
import com.dao.momentum.email.service.EmailService;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.organization.employee.query.mapper.UserRoleMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RedisTemplate<String, PasswordResetToken> passwordResetTokenRedisTemplate;
    private final EmailService emailService;

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

    @Transactional
    public void resetPassword(PasswordResetRequest request, String passwordResetToken) {
        if(!request.getPassword().equals(request.getVerifiedPassword())){
            throw new EmployeeException(ErrorCode.PASSWORD_NOT_CORRECT);
        }

        String empId = jwtTokenProvider.getUsernameFromJWT(passwordResetToken);
        log.info("empId = {}",empId);

        // Redis에 저장된 리프레시 토큰 조회
        PasswordResetToken storedPasswordResetToken = passwordResetTokenRedisTemplate.opsForValue().get(empId);
        if (storedPasswordResetToken == null) {
            throw new BadCredentialsException("해당 유저로 조회되는 리프레시 토큰 없음");
        }

        // 넘어온 리프레시 토큰과 Redis의 토큰 비교
        if (!storedPasswordResetToken.getToken().equals(passwordResetToken)) {
            throw new BadCredentialsException("리프레시 토큰 일치하지 않음");
        }

        Employee employee = employeeRepository.findByEmpId(Long.parseLong(empId))
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND)
                );

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        employee.setPassword(encodedPassword);

        //토큰 사용 완료후 제거
        passwordResetTokenRedisTemplate.delete(empId);
    }

    @Transactional
    public PasswordResetResponse resetPasswordRequest(PasswordResetLinkRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND)
                );

        String passwordResetToken = jwtTokenProvider.createPasswordResetToken(
                String.valueOf(employee.getEmpId())
        );

        PasswordResetToken redisPasswordResetToken = PasswordResetToken.builder()
                .token(passwordResetToken)
                .build();

        passwordResetTokenRedisTemplate.opsForValue().set(
                String.valueOf(employee.getEmpId()),
                redisPasswordResetToken,
                Duration.ofMinutes(10)
        );

        //이메일 처리
        emailService.sendPasswordResetEmail(employee,passwordResetToken);

        return PasswordResetResponse.builder()
                .message("비밀번호 재설정 메일이 전송되었습니다. 이메일을 확인해주세요.")
                .build();
    }

    @Transactional
    public PasswordChangeResponse changePassword(UserDetails username, PasswordChangeRequest request) {
        Long employeeId = Long.parseLong(username.getUsername());

        //사원 여부 검사
        Employee employee = employeeRepository.findByEmpId(employeeId).orElseThrow(
                () -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND)
        );

        //기존 비밀번호 검사
        if(!passwordEncoder.matches(request.getCurrentPassword(), employee.getPassword())){
            throw new EmployeeException(ErrorCode.PASSWORD_NOT_CORRECT);
        }

        //비밀번호 검사
        if(!request.getPassword().equals(request.getVerifiedPassword())){
            throw new EmployeeException(ErrorCode.PASSWORD_NOT_CORRECT);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        employee.setPassword(encodedPassword);

        return PasswordChangeResponse.builder()
                .message("비밀번호가 변경되었습니다.")
                .build();
    }
}
