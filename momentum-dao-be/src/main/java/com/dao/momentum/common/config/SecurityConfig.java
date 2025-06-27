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
//                            managerEndpoints(auth);
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
                "/employees/reset-password",
                "/employees/reset-password/request",
                "/swagger-ui/**",
                "/v3/api-docs/**"
        ).permitAll();
    }

    // 회원(로그인 사용자)만 접근 가능
    private void employeeEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auths) {
        auths.requestMatchers(
                "/employees/me"
        ).authenticated();

//        auths.requestMatchers(
//              ""
//        ).hasAuthority("EMPLOYEE");
    }

    // 팀장 전용
    private void managerEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auths) {
        auths.requestMatchers(
               ""
        ).hasAuthority("MANAGER");
    }

    // 마스터 관리자 전용
    private void masterEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auths) {
        auths.requestMatchers(
                "/position", "/position/**",
                "/employees/roles",
                "/employees/{empId}/roles"
                ).hasAuthority("MASTER")
                .requestMatchers(HttpMethod.GET,"/holiday").hasAuthority("MASTER")
                .requestMatchers(HttpMethod.POST, "/departments","/holiday").hasAuthority("MASTER")
                .requestMatchers(HttpMethod.PUT, "/company","/departments").hasAuthority("MASTER")
                .requestMatchers(HttpMethod.DELETE,"/departments/{deptId}").hasAuthority("MASTER");
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
                "/contracts", // GET, POST
                "/employees/csv", // GET, POST
                "/employees/appoints",
                "/employees/{empId}/hr-info"

        ).hasAnyAuthority("MASTER", "HR_MANAGER");

        auths.requestMatchers(
                HttpMethod.GET,
                "/works",
                "/employees/{empId}" // "/csv"는 여기서도 매칭되지만 위에서 먼저 처리했으므로 안 잡힘
        ).hasAnyAuthority("MASTER", "HR_MANAGER");

        auths.requestMatchers(
                HttpMethod.PUT,
                "/employees/{empId}"
        ).hasAnyAuthority("MASTER", "HR_MANAGER");

        auths.requestMatchers(
                HttpMethod.DELETE,
                "/contracts/{contractId}"
        ).hasAnyAuthority("MASTER", "HR_MANAGER");
    }

}
