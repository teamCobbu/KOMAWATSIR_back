package com.aendyear.komawatsir.auth;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private long expirationTime;
    private final KakaoUserService kakaoUserService;  // Kakao 사용자 정보를 가져오는 서비스

    public JwtTokenProvider(JwtConfig jwtConfig, KakaoUserService kakaoUserService) {
        this.secretKey = Base64.getEncoder().encodeToString(jwtConfig.getSecretKey().getBytes());
        this.expirationTime = jwtConfig.getExpirationTime();
        this.kakaoUserService = kakaoUserService;  // KakaoUserService 주입
    }

    // 토큰 생성 메서드
    public String createToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰 검증 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        }  catch (ExpiredJwtException e) {// 만료된 토큰 처리
            throw new JwtException("Token expired", e);
        } catch (MalformedJwtException e) {// 잘못된 형식의 토큰 처리
            throw new JwtException("Malformed token", e);
        } catch (JwtException | IllegalArgumentException e) {// 기타 예외 처리
            return false;
        }
    }

    // 토큰에서 사용자 정보 추출
    public String getUserId(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token).getBody().getSubject();
    }
    // HttpServletRequest에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 부분 제거
        }
        return null;
    }

    // 토큰에서 인증 정보 가져오기
    public Authentication getAuthentication(String token) {
        String userId = getUserId(token); // 카카오 사용자 ID를 토큰에서 한번만 추출
        KakaoUser kakaoUser = kakaoUserService.getKakaoUserInfo(token); // 카카오에서 사용자 정보 가져오기
        UserDetails userDetails = new CustomUserDetails(kakaoUser); // CustomUserDetails는 사용자의 정보를 포함한 클래스

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
