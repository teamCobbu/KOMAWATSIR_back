package com.aendyear.komawatsir.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component //빈 자동등록때문에 나중에 활성화
public class JwtAuthenticationFilter extends OncePerRequestFilter { // JWT 토큰을 검증
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
            String httpMethod = request.getMethod();

            // 인증 건너뛰기
            if (isExcluded(requestURI, httpMethod)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 추출 및 검증
            String token = jwtTokenProvider.resolveToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth); // 인증 처리
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private boolean isExcluded(String requestURI, String httpMethod) {
        // 인증이 필요없는 경로
        return requestURI.equals("/api/users/kakao/login-test") ||
                requestURI.equals("/api/users/kakao/loginPage") ||
                requestURI.equals("/api/users/kakao/logout") ||
                requestURI.equals("/**") ||
                (httpMethod.equals("POST") && requestURI.matches("^/api/users/\\d+/receivers$")) ||
                (httpMethod.equals("GET")) && requestURI.equals("/api/inquiry/{userId}/validate/url");
    }

}
