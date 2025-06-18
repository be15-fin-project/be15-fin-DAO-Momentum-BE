package com.dao.momentum.common.auth.application.service;

import com.dao.momentum.common.auth.application.dto.request.LoginRequest;
import com.dao.momentum.common.auth.application.dto.response.LoginResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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
}