package com.aendyear.komawatsir.auth;

import com.aendyear.komawatsir.dto.UserDto;
import com.aendyear.komawatsir.entity.User;
import com.aendyear.komawatsir.repository.UserRepository;
import com.aendyear.komawatsir.service.Mapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoUserService { //사용자 정보조회

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    public KakaoUserService(RestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    public UserDto findByKakaoId(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId).orElse(null);
        return Mapper.toDto(user);
    }

    public User getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_USER_INFO_URL,
                HttpMethod.GET,
                entity,
                String.class
        );

        String responseBody = response.getBody();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            return User.builder()
                    .kakaoId(rootNode.get("id").asText())
                    .name(rootNode.get("properties").get("nickname").asText())
                    .isSmsAllowed(false) // 기본값
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Kakao user info", e);
        }
    }
}