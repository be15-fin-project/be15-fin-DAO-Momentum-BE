package com.dao.momentum.common.auth.application.controller;


import com.dao.momentum.common.auth.application.dto.request.LoginRequest;
import com.dao.momentum.common.auth.application.dto.request.PasswordChangeRequest;
import com.dao.momentum.common.auth.application.dto.request.PasswordResetLinkRequest;
import com.dao.momentum.common.auth.application.dto.request.PasswordResetRequest;
import com.dao.momentum.common.auth.application.dto.response.LoginResponse;
import com.dao.momentum.common.auth.application.dto.response.PasswordChangeResponse;
import com.dao.momentum.common.auth.application.dto.response.PasswordResetResponse;
import com.dao.momentum.common.auth.application.dto.response.TokenResponse;
import com.dao.momentum.common.auth.application.service.AuthService;
import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @Operation(summary = "로그인", description = "email과 password을 입력받아 로그인하고 토큰을 발급받는다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest){
        LoginResponse response = authService.login(loginRequest);
        ResponseCookie cookie = createRefreshTokenCookie(response.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(response));
    }

    @Operation(summary = "로그아웃", description = "사용자는 인증 토큰을 삭제하며 로그아웃한다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {

        authService.logout(refreshToken);

        ResponseCookie deleteCookie = createDeleteRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(ApiResponse.success(null));
    }

    @Operation(summary = "토큰 재발급", description = "만료된 토큰을 재발급받아 재로그인 없이 인증 상태를 유지한다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // refreshToken이 없으면 401 반환
        }
        TokenResponse tokenResponse = authService.refreshToken(refreshToken);
        ResponseCookie cookie = createRefreshTokenCookie(tokenResponse.getRefreshToken());  // refreshToken 쿠키 생성
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(tokenResponse));
    }

    @Operation(summary = "비밀번호 재설정", description = "사원은 비밀번호 재설정을 할 수 있다. 비밀번호, 확인 비밀번호, 토큰으로 요청한다.")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody @Valid PasswordResetRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ){
        String passwordResetToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            passwordResetToken = authorizationHeader.substring(7); // "Bearer " 뒤 토큰만 추출
        }
        authService.resetPassword(request, passwordResetToken);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @Operation(summary = "비밀번호 재설정 요청", description = "비회원은 자신의 이메일을 입력하여 비밀번호 재설정을 요청할 수 있다.")
    @PostMapping("/reset-password/request")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> resetPasswordRequest(
            @RequestBody @Valid PasswordResetLinkRequest request
    ){
        PasswordResetResponse response = authService.resetPasswordRequest(request);

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @Operation(summary = "비밀번호 변경", description = "사원은 권한이 있는 상태에서 자신의 비밀번호를 변경할 수 있다. 비밀번호, 확인 비밀번호를 필요로 한다.")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<PasswordChangeResponse>> changePassword(
            @AuthenticationPrincipal UserDetails username,
            @RequestBody @Valid PasswordChangeRequest request
    ) {
        PasswordChangeResponse response = authService.changePassword(username,request);

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                // .secure(true) // 운영 환경에서 HTTPS 사용 시 활성화
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
    }

    private ResponseCookie createDeleteRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                // .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }

    @ExceptionHandler(EmployeeException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmployeeException(EmployeeException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }
}
