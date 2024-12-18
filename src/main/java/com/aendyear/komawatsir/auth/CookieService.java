package com.aendyear.komawatsir.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class CookieService {
    private static final String DOMAIN = "xn--299au8vhphgpd.com"; // 도메인 설정
    // 쿠키 추가 메서드
    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setDomain(DOMAIN);
        response.addCookie(cookie);
    }

    // 쿠키 삭제 메서드
    public void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setDomain(DOMAIN);
        response.addCookie(cookie);
    }
}