package com.aendyear.komawatsir.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter { // JWT 토큰을 검증
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        try {
            logger.info("JwtAuthenticationFilter: URI=" + requestURI + ", Method=" + httpMethod);

            // 인증 건너뛰기
            if (isExcluded(requestURI, httpMethod)) {
                logger.info("JwtAuthenticationFilter: Excluded path. URI=" + requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            if (requestURI.startsWith("/actuator")) {
                filterChain.doFilter(request, response);
                return;
            }

            if (requestURI.startsWith("/api/users/token/validate")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 추출 및 검증
            String token = jwtTokenProvider.resolveToken(request);
            logger.info("JwtAuthenticationFilter: Extracted token=" + token);

            if (token == null || !jwtTokenProvider.validateToken(token)) {
                logger.warn("JwtAuthenticationFilter: Invalid or missing token. URI=" + requestURI);
                HttpSession session = request.getSession(false);
                if (session != null) {
                    logger.info("JwtAuthenticationFilter: Invalidating session.");
                    session.invalidate();
                }
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            } else {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                logger.info("JwtAuthenticationFilter: Authentication success. User=" + auth.getName());
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info("JwtAuthenticationFilter: SecurityContext set. Authentication=" + SecurityContextHolder.getContext().getAuthentication());
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("JwtAuthenticationFilter: Internal server error. URI=" + requestURI, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private boolean isExcluded(String requestURI, String httpMethod) {
        // 인증이 필요없는 경로
        return requestURI.equals("/api/users/kakao/login-test") ||
                requestURI.equals("/api/users/kakao/loginPage") ||
                requestURI.equals("/api/users/logout") ||
                (httpMethod.equals("POST") && requestURI.matches("^/api/users/\\d+/receivers$")) ||
                (httpMethod.equals("GET")) && requestURI.equals("/api/inquiry/validate/url");
    }
}
