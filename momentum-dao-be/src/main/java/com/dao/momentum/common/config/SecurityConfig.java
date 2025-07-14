package com.dao.momentum.common.config;

import com.dao.momentum.common.jwt.JwtAuthenticationFilter;
import com.dao.momentum.common.jwt.JwtTokenProvider;
import com.dao.momentum.common.jwt.RestAccessDeniedHandler;
import com.dao.momentum.common.jwt.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // @PreAuthorize, @PostAuthorize 사용을 위해 설정
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(restAuthenticationEntryPoint) // 인증 실패
                                .accessDeniedHandler(restAccessDeniedHandler)) // 인가 실패
                .authorizeHttpRequests(
                        auth -> {
                              permitAllEndpoints(auth);
                              employeeEndpoints(auth);
                              managerEndpoints(auth);
                              masterEndpoints(auth);
//                            hrManagerEndpoints(auth);
//                            bookkeepingEndpoints(auth);

                            // 공통 엔드포인트
                            adminEndpoints(auth);

                            // 이 외의 요청은 인증 필요
                            auth.anyRequest().authenticated();
                        }
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        /* CORS 설정 */
        http.cors(cors -> cors
                .configurationSource(corsConfigurationSource()));
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173"); // 허용할 도메인
        config.addAllowedOrigin("https://momentum-dao.site");
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
        config.setAllowCredentials(true);// 자격 증명(쿠키 등) 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);// 모든 경로에 대해 설정
        return source;
    }

    // 인증 없이 접근 허용
    private void permitAllEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auths) {
        // 로그인 및 토큰 관련 (모두 허용)
        auths.requestMatchers(
                "/employees/login",
                "/employees",
                "/employees/refresh",
                "/employees/reset-password",
                "/employees/reset-password/request",
                "/swagger-ui/**",
                "/v3/api-docs/**"
        ).permitAll();
    }

    // 회원(로그인 사용자)만 접근 가능
    private void employeeEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auths) {
        auths.requestMatchers(
                "/employees/me",
                "/remaining/refresh",
                "/remaining/dayoff",

                // KPI
                "/kpi/dashboard",
                "/kpi/{kpiId}",
                "/kpi",
                "/kpi/{kpiId}/withdraw",
                "/kpi/{kpiId}",
                "/kpi/{kpiId}/progress",
                "/kpi/excel",
                "/kpi/my-statistics",
                "/kpi/my-timeseries",
                "/kpi/my-list",
                // 평가
                "/evaluation/tasks",
                "/eval-forms/{formId}",
                "/evaluations/submit",
                "/evaluations/form-tree",
                "/evaluations/form-property",
                "/evaluations/rounds",
                "/evaluations/rounds/**",
                "/evaluations/roundNo",
                "/evaluations/roundStatus",
                "/evaluations/hr",
                "/evaluations/hr/{resultId}",
                // 인사 평가
                "/hr-objections/{evaluationId}",
                "/hr-objections/my",
                "/hr-objections/my/{objectionId}",
                "/hr-objections/{objectionId}",
                
                // 근속 전망
                "/retention/contact/my",
                "/retention/contact/{retentionId}",
                "/retention-contacts/{retentionId}/response"
        ).authenticated();

//        auths.requestMatchers(
//              ""
//        ).hasAuthority("EMPLOYEE");
    }

    // 팀장 전용
    private void managerEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auths) {
        auths.requestMatchers(
                //KPI
                "/kpi/requests",
                "/kpi/{kpiId}/approval",
                "/kpi/{kpiId}/cancel/approval",
                // 인사 평가
                "/hr-objections/requests",
                "/hr-objections/requests/{objectionId}",
                "/hr-objections/process"
        ).hasAuthority("MANAGER");
    }

    // 마스터 관리자 전용
    private void masterEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auths) {
        auths.requestMatchers(
                "/employees/roles",
                "/employees/{empId}/roles"
        ).hasAuthority("MASTER");

        auths.requestMatchers(HttpMethod.GET,
                "/holiday",
                "/employees/ids"
        ).hasAuthority("MASTER");

        auths.requestMatchers(HttpMethod.POST,
                "/employees/batch-token",
                "/departments",
                "/holiday",
                "/position"
        ).hasAuthority("MASTER");

        auths.requestMatchers(HttpMethod.PUT,
                "/company",
                "/departments",
                "/position"
        ).hasAuthority("MASTER");

        auths.requestMatchers(HttpMethod.DELETE,
                "/departments/{deptId}"
        ).hasAuthority("MASTER");

    }

    // 인사관리자 전용
    private void hrManagerEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auths) {
        auths.requestMatchers(
                ""
        ).hasAuthority("HR_MANAGER");
    }

    // 경리팀 전용
    private void bookkeepingEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auths) {
        auths.requestMatchers(
                ""
        ).hasAuthority("BOOKKEEPING");
    }

    // 마스터 관리자 및 인사 관리자 공용
    private void adminEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auths) {
        auths.requestMatchers(
                "/position",
                "/contracts", // GET, POST
                "/employees/csv", // GET, POST
                "/employees/appoints",
                "/employees/{empId}/hr-info",


                // KPI 통계 및 전체 조회
                "/kpi/statistics",
                "/kpi/timeseries",
                "/kpi/list",
                "/kpi/employee-summary",

                // 평가 결과 조회 (사원 간, 조직, 자가진단)
                "/evaluation/results/peer",
                "/evaluation/results/peer/{resultId}",
                "/evaluation/results/org",
                "/evaluation/results/org/{resultId}",
                "/evaluations/self",
                "/evaluations/self/{evalId}",

                // 회차 관리
                "/evaluations/forms",
                "/evaluations/hr/{roundNo}/criteria",

                // 근속 전망 통계 및 회차
                "/retention/forecast",
                "/retention/{retentionId}",
                "/retention/statistics/overview",
                "/retention/statistics/stability-distribution/overall",
                "/retention/statistics/stability-distribution",
                "/retention/statistics/timeseries",
                "/retention/rounds",
                "/retention-forecasts",
                "/retention-forecasts/{roundId}",
                "/retention-supports/excel",

                // 면담 기록 및 요청
                "/retention/contact",
                "/retention/contact/{retentionId}",
                "/retention-contacts/{retentionId}/request",
                "/retention-contacts",
                "/retention-contacts/{retentionId}",
                "/retention-contacts/{retentionId}/feedback"
        ).hasAnyAuthority("MASTER", "HR_MANAGER");

        auths.requestMatchers(
                HttpMethod.GET,
                "/works",
                "/employees/{empId}", // "/csv"는 여기서도 매칭되지만 위에서 먼저 처리했으므로 안 잡힘
                "/departments/leaf"
        ).hasAnyAuthority("MASTER", "HR_MANAGER");

        auths.requestMatchers(
                HttpMethod.PUT,
                "/employees/{empId}"
        ).hasAnyAuthority("MASTER", "HR_MANAGER");

        auths.requestMatchers(
                HttpMethod.DELETE,
                "/contracts/{contractId}"
        ).hasAnyAuthority("MASTER", "HR_MANAGER");

        auths.requestMatchers(
                HttpMethod.GET,
                "/admin/approval/documents"
        ).hasAnyAuthority("MASTER");
    }

}
