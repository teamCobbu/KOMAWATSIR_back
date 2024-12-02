package com.aendyear.komawatsir.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoAuthService {//Access Token을 요청

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_LOGOUT_URL = "https://kapi.kakao.com/v1/user/logout";
    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

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

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_TOKEN_URL, HttpMethod.POST, requestEntity, String.class
        );

        // 응답에서 Access Token 파싱
        String responseBody = response.getBody();
        return responseBody; // 실제로는 Access Token을 추출하여 반환해야 합니다
    }

    // 카카오 로그아웃 요청
    public boolean logout(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // 사용자의 access token을 헤더에 담아서 요청

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_LOGOUT_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getStatusCode().is2xxSuccessful(); // 로그아웃 성공 여부 반환
    }

    //카카오 탈퇴 처리
    public boolean unlinkUser(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
//        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("",headers);

        System.out.println("entity + " + entity);

        try {
            // RestTemplate의 exchange 메서드 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_UNLINK_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            } else {
                System.out.println("Response Status: " + response.getStatusCode());
                System.out.println("Response Body: " + response.getBody());
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error occurred while unlinking: " + e.getMessage());
            return false;
        }
    }
}