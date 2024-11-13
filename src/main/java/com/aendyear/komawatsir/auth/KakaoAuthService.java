package com.aendyear.komawatsir.auth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoAuthService {//Access Token을 요청

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    private final RestTemplate restTemplate;

    public KakaoAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAccessToken(String code, String clientId, String redirectUri) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        // 바디 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 카카오 토큰 요청
        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_TOKEN_URL, HttpMethod.POST, requestEntity, String.class
        );

        // 응답에서 Access Token 파싱
        String responseBody = response.getBody();
        // JSON 파싱 로직 추가 (예시로 바로 리턴)
        return responseBody; // 실제로는 Access Token을 추출하여 반환해야 합니다
    }
}