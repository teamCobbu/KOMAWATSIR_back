package com.aendyear.komawatsir.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime; // 만료 시간(1시간)

    public String getSecretKey() {
        return secretKey;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}

