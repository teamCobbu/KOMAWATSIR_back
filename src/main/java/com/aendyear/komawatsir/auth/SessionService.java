package com.aendyear.komawatsir.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    public String getKakaoAccessTokenFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("kakao_access_token");
        }
        return null;
    }

    public void addKakaoAccessTokenToSession(String key,HttpServletRequest request, String accessToken) {
        HttpSession session = request.getSession(true);
        session.setAttribute(key, accessToken);
    }

    public void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
