package com.aendyear.komawatsir.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Order(1)
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    public static final int SC_TOO_MANY_REQUESTS = 429;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private static final List<String> BLOCKED_USER_AGENTS = List.of("BadBot"
            ,"MaliciousCrawler"
            ,"PostmanRuntime"
            ,"curl"
            ,"wget");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent != null && isBlockedUserAgent(userAgent)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied: Your User-Agent is blocked.");
            return;
        }

        String ipAddress = request.getRemoteAddr(); // 클라이언트 IP 가져오기
        Bucket bucket = buckets.computeIfAbsent(ipAddress, this::createNewBucket);

        if (bucket.tryConsume(1)) {
            // 토큰 소모 성공 -> 요청 허용
            filterChain.doFilter(request, response);
        } else {
            // 토큰 부족 -> HTTP 429 반환
            response.setStatus(SC_TOO_MANY_REQUESTS);
            response.getWriter().write("Too many requests");
        }
    }

    private boolean isBlockedUserAgent(String userAgent) {
        return BLOCKED_USER_AGENTS.stream().anyMatch(userAgent::contains);
    }

    private Bucket createNewBucket(String ipAddress) {
        Bandwidth limit = Bandwidth.classic(50, Refill.greedy(10, Duration.ofMinutes(1))); // 분당 10 요청
        return Bucket4j.builder().addLimit(limit).build();
    }
}