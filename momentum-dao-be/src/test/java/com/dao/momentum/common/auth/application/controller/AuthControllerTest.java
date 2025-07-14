package com.dao.momentum.common.auth.application.controller;

import com.dao.momentum.common.auth.application.dto.request.LoginRequest;
import com.dao.momentum.common.auth.application.dto.response.LoginResponse;
import com.dao.momentum.common.auth.application.dto.response.TokenResponse;
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
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;

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
                .build();

        when(authService.login(request)).thenReturn(loginResponse);
        when(authService.getRefreshToken(request)).thenReturn("refresh-token");

        // when
        ResponseEntity<ApiResponse<LoginResponse>> responseEntity = authController.login(request);

        // then
        assertEquals(200, responseEntity.getStatusCodeValue());

        // 바디 내용 검증
        ApiResponse<LoginResponse> body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("access-token", body.getData().getAccessToken());

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
    @DisplayName("로그아웃 성공_리프레시 토큰 없어도")
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

    @Test
    @DisplayName("토큰 갱신_성공")
    void refreshToken_success(){
        //given
        String refreshToken = "refreshToken";
        TokenResponse tokenResponse = TokenResponse
                .builder()
                .accessToken("accessToken")
                .refreshToken("newRefreshToken")
                .build();

        when(authService.refreshToken(refreshToken)).thenReturn(tokenResponse);

        //when
        ResponseEntity<ApiResponse<TokenResponse>> response = authController.refreshToken(refreshToken);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE).contains("refreshToken="));
    }

    @Test
    @DisplayName("토큰 갱신 실패_조회되는 리프레시 토큰 없음")
    void refreshTokenFail_tokenNotFound(){
        //given
        String refreshToken = "refreshToken";
        BadCredentialsException exception = new BadCredentialsException("해당 유저로 조회되는 리프레시 토큰 없음");

        when(authService.refreshToken(refreshToken)).thenThrow(BadCredentialsException.class);

        //when
        BadCredentialsException newException = assertThrows(
                BadCredentialsException.class,
                () -> authController.refreshToken(refreshToken));
    }

    @Test
    @DisplayName("토큰 갱신 실패_유효하지 않은 사원")
    void refreshTokenFail_EmployeeException() {
        // given
        String invalidRefreshToken = "invalid.token";
        EmployeeException exception = new EmployeeException(ErrorCode.INVALID_CREDENTIALS);

        // when
        when(authService.refreshToken(anyString())).thenThrow(exception);

        // then
        EmployeeException newException = assertThrows(
                EmployeeException.class,
                () -> authController.refreshToken(invalidRefreshToken));
    }
}