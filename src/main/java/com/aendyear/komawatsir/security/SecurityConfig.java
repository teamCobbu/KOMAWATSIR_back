package com.aendyear.komawatsir.security;

import com.aendyear.komawatsir.auth.JwtAuthenticationFilter;
import com.aendyear.komawatsir.auth.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {//JWT 토큰을 생성하고 검증
    private final JwtAuthenticationFilter jwtAuthenticationFilter ;
    private final RateLimitingFilter rateLimitingFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter , RateLimitingFilter rateLimitingFilter) {
        this.jwtAuthenticationFilter  = jwtAuthenticationFilter ;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**").permitAll() // Actuator 경로 허용
                        .requestMatchers("/api/users/kakao/loginPage", "/api/users/kakao/login-test").permitAll()
                        .requestMatchers("/api/users/logout").permitAll()
                        .requestMatchers("/api/inquiry/validate/url").permitAll()
                        .requestMatchers("/api/users/*/receivers").permitAll()
                        .requestMatchers("/api/users/token/validate/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(rateLimitingFilter, JwtAuthenticationFilter.class) // 순서 설정
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new DebugLoggingFilter(), UsernamePasswordAuthenticationFilter.class); // 디버깅 필터 추가
        return http.build();
    }

    class DebugLoggingFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException , IOException {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String user = (auth != null) ? auth.getName() : "Anonymous";
            System.out.println("DebugLoggingFilter: URI=" + request.getRequestURI() + ", Method=" + request.getMethod());
            filterChain.doFilter(request, response);
        }
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("https://xn--299au8vhphgpd.com");  // React 앱 URL 허용
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedMethod("*");  // 모든 HTTP 메서드 허용
        corsConfig.addAllowedHeader("*");  // 모든 HTTP 헤더 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);  // 모든 경로에 대해 CORS 설정

        return source;
    }
}