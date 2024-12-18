package com.aendyear.komawatsir.auth;

import com.aendyear.komawatsir.entity.User;
import com.aendyear.komawatsir.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieService cookieService;
    private final SessionService sessionService;
    private final UserRepository userRepository;

    @Autowired
    public AuthService(JwtTokenProvider jwtTokenProvider, CookieService cookieService, SessionService sessionService, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieService = cookieService;
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    public void addJwtToCookie(String kakaoId, HttpServletResponse response) {
        String jwtToken = jwtTokenProvider.createToken(kakaoId);
        cookieService.addCookie(response, "JWT", jwtToken, 3600);
    }

    public void deleteJwtCookie(HttpServletResponse response) {
        cookieService.deleteCookie(response, "JWT");
    }

    public void addAccessTokenToSession(String accessToken, HttpServletRequest request) {
        sessionService.addKakaoAccessTokenToSession("kakao_access_token", request, accessToken);
    }

    public void invalidateSession(HttpServletRequest request){
        sessionService.invalidateSession(request);
    }

    public boolean validateToken(Integer userId, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);

        // 토큰이 null이거나 유효하지 않으면 false 반환
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return false;
        }

        // 토큰에서 kakaoId 추출 후 DB 조회 및 검증
        String kakaoId = jwtTokenProvider.getUserId(token);
        return userRepository.findByKakaoId(kakaoId)
                .map(user -> user.getId().equals(userId))
                .orElse(false); // 사용자가 없으면 false 반환
    }
}
