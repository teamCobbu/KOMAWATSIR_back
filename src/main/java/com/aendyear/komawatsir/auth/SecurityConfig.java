package com.aendyear.komawatsir.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {//JWT 토큰을 생성하고 검증

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**","/api/users/kakao/").permitAll()  // 인증 없이 접근 가능한 경로
                        .anyRequest().authenticated()); // 나머지 요청은 인증 필요
            // .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            //                  UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
