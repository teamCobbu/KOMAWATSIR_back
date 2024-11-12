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

    public KakaoUser getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = KAKAO_USER_INFO_URL;

        // 헤더에 accessToken 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<KakaoUser> response = restTemplate.exchange(url, HttpMethod.GET, entity, KakaoUser.class);

        return response.getBody();
    }
}
