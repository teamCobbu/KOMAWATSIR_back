package com.aendyear.komawatsir.auth;

import com.aendyear.komawatsir.dto.UserDto;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final long expirationTime;
    private final String issuer;
    private final KakaoUserService kakaoUserService;  // Kakao 사용자 정보를 가져오는 서비스

    public JwtTokenProvider(JwtConfig jwtConfig, KakaoUserService kakaoUserService) {
        this.secretKey = Base64.getEncoder().encodeToString(jwtConfig.getSecretKey().getBytes());
        this.expirationTime = jwtConfig.getExpirationTime();
        this.issuer = jwtConfig.getIssuer();
        this.kakaoUserService = kakaoUserService;  // KakaoUserService 주입
    }

    // 토큰 생성 메서드
    public String createToken(String kakaoId) {
        Claims claims = Jwts.claims().setSubject(kakaoId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS256, new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"))
                .compact();
    }

    // 토큰 검증 메서드
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String subject = claims.getSubject();
            if(subject == null || subject.isEmpty()){
                throw new JwtException("subject is empty");
            }

            String issuer = claims.getIssuer();
            if (issuer == null || issuer.isEmpty()){
                throw new JwtException("issuer is empty");
            }

            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())){
                throw new JwtException("token has expired");
            }
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token expired", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("Malformed token", e);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 사용자 정보 추출
    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // HttpServletRequest에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("JWT".equals(cookie.getName())) {
                        return cookie.getValue();  // 쿠키에서 JWT 값을 반환
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve token", e);  // 예외 메시지에 추가 정보를 포함
        }
    }

    public Authentication getAuthentication(String token) {
        UserDto kakaoUser = kakaoUserService.findByKakaoId(getUserId(token));
        if (kakaoUser == null) {
            throw new RuntimeException("User not found: " + kakaoUser.getId()); // 예외 발생
        }
        UserDetails userDetails = new CustomUserDetails(kakaoUser); // CustomUserDetails는 사용자의 정보를 포함한 클래스
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}