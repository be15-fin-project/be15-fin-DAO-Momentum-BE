package com.dao.momentum.common.auth.application.service;

import com.dao.momentum.common.auth.application.dto.request.LoginRequest;
import com.dao.momentum.common.auth.application.dto.response.LoginResponse;
import com.dao.momentum.common.auth.application.dto.response.TokenResponse;
import com.dao.momentum.common.auth.domain.aggregate.RefreshToken;
import com.dao.momentum.common.jwt.JwtTokenProvider;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.Gender;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.organization.employee.query.mapper.UserRoleMapper;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperties;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private UserRoleMapper userRoleMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RedisTemplate<String, RefreshToken> redisTemplate;
    @Mock
    private ValueOperations<String, RefreshToken> valueOperations;
    @InjectMocks
    private AuthService authService;

    static LoginRequest loginRequest;
    static Employee employee;

    @BeforeAll
    static void setEntities(){
        loginRequest= new LoginRequest(
                "john.doe@example.com",
                "password01@"
        );

        employee = Employee.builder()
                .empId(1L)
                .empNo("EMP001")
                .email("john.doe@example.com")
                .deptId(101)
                .positionId(1)
                .password("securedPassword")
                .name("John Doe")
                .gender(Gender.M)
                .address("123 Main Street, Seoul")
                .contact("010-1234-5678")
                .joinDate(LocalDate.of(2023, 3, 1))
                .status(Status.EMPLOYED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .remainingDayoffHours(80)
                .remainingRefreshDays(3)
                .birthDate(LocalDate.of(1990, 5, 15))
                .build();

    }

    @Test
    @DisplayName("로그인_성공")
    void login_success(){
        List<String> roles = List.of("ROLE_USER");
        String[] employeeRoleArray = roles.toArray(new String[0]);

        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(employeeRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.ofNullable(employee));
        when(passwordEncoder.matches(loginRequest.getPassword(),employee.getPassword())).thenReturn(true);
        when(userRoleMapper.findByEmpId(employee.getEmpId())).thenReturn(roles);
        when(jwtTokenProvider.createToken(String.valueOf(employee.getEmpId()), employeeRoleArray)).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(String.valueOf(employee.getEmpId()), employeeRoleArray)).thenReturn(refreshToken);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        LoginResponse response = authService.login(loginRequest);

        // then
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertArrayEquals(roles.toArray(), response.getUserRoles());
    }

    @Test
    @DisplayName("로그인 실패_유효하지 않은 이메일")
    void login_fail_invalid_email() {
        // given
        LoginRequest request = new LoginRequest("notfound@example.com", "password");

        when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThrows(EmployeeException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("로그인 실패_유효하지 않은 비밀번호")
    void login_fail_invalid_password() {
        // given
        LoginRequest request = new LoginRequest("john.doe@example.com", "wrongPassword");

        Employee employee = Employee.builder()
                .empId(1L)
                .email("john.doe@example.com")
                .password("encodedPassword")
                .build();

        when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // when & then
        assertThrows(EmployeeException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("로그아웃_성공")
    void logout_success(){
        //given
        String refreshToken = "refresh-token";
        String userId = "userId@example.com";
        //when
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(refreshToken)).thenReturn(userId);
        authService.logout(refreshToken);

        // then
        verify(jwtTokenProvider).validateToken(refreshToken);
        verify(jwtTokenProvider).getUsernameFromJWT(refreshToken);
        verify(redisTemplate).delete(userId);
    }

    @Test
    @DisplayName("로그아웃 실패_만료된 토큰")
    void logout_invalidToken_throwsException() {
        // given
        String invalidToken = "invalid-token";

        doThrow(new JwtException("Invalid token"))
                .when(jwtTokenProvider).validateToken(invalidToken);

        // when & then
        JwtException exception = assertThrows(JwtException.class, () -> {
            authService.logout(invalidToken);
        });

        assertEquals("Invalid token", exception.getMessage());
        verify(jwtTokenProvider).validateToken(invalidToken);
        verify(redisTemplate, never()).delete((String) any());
    }

    @Test
    @DisplayName("토큰갱신 성공")
    void refreshToken_success() {
        // given
        String oldRefreshToken = "old.refresh.token";
        String empId = "123";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";
        RefreshToken storedRefreshToken = new RefreshToken("old.refresh.token");
        Employee mockEmployee = Employee.builder()
                .empId(123L)
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        List<String> roles = List.of("ROLE_USER");

        // mocking
        when(jwtTokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(oldRefreshToken)).thenReturn(empId);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("123")).thenReturn(storedRefreshToken);
        when(employeeRepository.findByEmpId(123L)).thenReturn(Optional.of(mockEmployee));
        when(userRoleMapper.findByEmpId(123L)).thenReturn(roles);
        when(jwtTokenProvider.createToken(empId, roles.toArray(new String[0]))).thenReturn(newAccessToken);
        when(jwtTokenProvider.createRefreshToken(empId, roles.toArray(new String[0]))).thenReturn(newRefreshToken);

        // when
        TokenResponse response = authService.refreshToken(oldRefreshToken);

        // then
        assertEquals(newAccessToken, response.getAccessToken());
        assertEquals(newRefreshToken, response.getRefreshToken());
    }

    @Test
    @DisplayName("토큰갱신 실패_조회되는 리프레시토큰 없음")
    void refreshTokenFail_refreshTokenNotFound() {
        // given
        String oldRefreshToken = "old.refresh.token";
        String empId = "123";

        // mocking
        when(jwtTokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(oldRefreshToken)).thenReturn(empId);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(empId)).thenReturn(null);

        // when
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.refreshToken(oldRefreshToken)
        );

        // then
        assertEquals("해당 유저로 조회되는 리프레시 토큰 없음", exception.getMessage());
        verify(jwtTokenProvider).validateToken(oldRefreshToken);
        verify(valueOperations, never()).set((String) any(),any());
    }

    @Test
    @DisplayName("토큰갱신 실패_토큰 불일치")
    void refreshTokenFail_tokenDiscord() {
        // given
        String oldRefreshToken = "old.refresh.token";
        String empId = "123";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";
        RefreshToken storedRefreshToken = new RefreshToken("discord.refresh.token");
        Employee mockEmployee = Employee.builder()
                .empId(123L)
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        List<String> roles = List.of("ROLE_USER");

        // mocking
        when(jwtTokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(oldRefreshToken)).thenReturn(empId);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("123")).thenReturn(storedRefreshToken);

        //when
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.refreshToken(oldRefreshToken)
        );

        // then
        assertEquals("리프레시 토큰 일치하지 않음", exception.getMessage());
        verify(jwtTokenProvider).validateToken(oldRefreshToken);
        verify(valueOperations, never()).set((String) any(),any());
    }

    @Test
    @DisplayName("토큰갱신 실패_회원 조회 실패")
    void refreshTokenFail_EmployeeNotFound() {
        // given
        String oldRefreshToken = "old.refresh.token";
        String empId = "123";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";
        RefreshToken storedRefreshToken = new RefreshToken("old.refresh.token");
        Employee mockEmployee = Employee.builder()
                .empId(123L)
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        List<String> roles = List.of("ROLE_USER");

        // mocking
        when(jwtTokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(oldRefreshToken)).thenReturn(empId);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("123")).thenReturn(storedRefreshToken);
        when(employeeRepository.findByEmpId(123L)).thenReturn(Optional.empty());

        // when
        EmployeeException exception = assertThrows(
                EmployeeException.class,
                () -> authService.refreshToken(oldRefreshToken)
        );

        // then
        verify(jwtTokenProvider).validateToken(oldRefreshToken);
        verify(valueOperations, never()).set((String) any(),any());
    }
}