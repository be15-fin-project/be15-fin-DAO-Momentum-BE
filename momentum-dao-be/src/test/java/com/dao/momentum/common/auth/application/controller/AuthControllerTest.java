package com.dao.momentum.common.auth.application.controller;

import com.dao.momentum.common.auth.application.dto.request.LoginRequest;
import com.dao.momentum.common.auth.application.dto.response.LoginResponse;
import com.dao.momentum.common.auth.application.service.AuthService;
import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("로그인 성공")
    void login_success_returnsExpectedResponse() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "securedpassword");

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .userRoles(new String[]{"ROLE_USER"})
                .build();

        when(authService.login(request)).thenReturn(loginResponse);

        // when
        ResponseEntity<ApiResponse<LoginResponse>> responseEntity = authController.login(request);

        // then
        assertEquals(200, responseEntity.getStatusCodeValue());

        // 바디 내용 검증
        ApiResponse<LoginResponse> body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("access-token", body.getData().getAccessToken());
        assertEquals("refresh-token", body.getData().getRefreshToken());
        assertArrayEquals(new String[]{"ROLE_USER"}, body.getData().getUserRoles());

        // 헤더 검증
        String setCookieHeader = responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookieHeader);
        assertTrue(setCookieHeader.contains("refresh-token")); // 쿠키 이름에 따라 조정
    }

    @Test
    @DisplayName("로그인 실패_유효하지 않은 비밀번호")
    void login_employeeNotFound_throwsEmployeeException() {
        // given
        LoginRequest request = new LoginRequest("notfound@example.com", "password");

        when(authService.login(request))
                .thenThrow(new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // when & then
        EmployeeException exception = assertThrows(EmployeeException.class, () -> {
            authController.login(request);
        });

        assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패_유효하지 않은 비밀번호")
    void login_invalidPassword_throwsEmployeeException() {
        // given
        LoginRequest request = new LoginRequest("user@example.com", "wrongpassword");

        when(authService.login(request))
                .thenThrow(new EmployeeException(ErrorCode.INVALID_CREDENTIALS));

        // when & then
        EmployeeException exception = assertThrows(EmployeeException.class, () -> {
            authController.login(request);
        });

        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그아웃_성공")
    void logout_success(){
        //given
        String refreshToken = "refresh_Token";
        doNothing().when(authService).logout(refreshToken);

        //when
        ResponseEntity<ApiResponse<Void>> response = authController.logout(refreshToken);
        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().get(HttpHeaders.SET_COOKIE));
        assertTrue(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE).contains("refreshToken="));
        assertTrue(response.getBody().isSuccess());
        assertNull(response.getBody().getData());

        verify(authService).logout(refreshToken);

    }

    @Test
    void logout_whenRefreshTokenIsNull_shouldStillSucceed() {
        // when
        ResponseEntity<ApiResponse<Void>> response = authController.logout(null);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().get(HttpHeaders.SET_COOKIE));
        assertTrue(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE).contains("refreshToken="));
        assertTrue(response.getBody().isSuccess());
        assertNull(response.getBody().getData());

        verify(authService).logout(null);
    }
}