package com.aendyear.komawatsir.auth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoUserService {

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate;

    // RestTemplate 빈 주입
    public KakaoUserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public KakaoUser getKakaoUserInfo(String accessToken) {
        String url = KAKAO_USER_INFO_URL;

        // 헤더에 accessToken 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<KakaoUser> response = restTemplate.exchange(url, HttpMethod.GET, entity, KakaoUser.class);

        return response.getBody();
    }
}
