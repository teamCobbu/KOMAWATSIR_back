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
        // DB에서 KakaoId로 사용자 정보 조회
        User user = userRepository.findByKakaoId(kakaoId).orElse(null);
        // User → UserDto 변환
        return Mapper.toDto(user);
    }


    public UserDto getKakaoUserInfo(String accessToken) {
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

            UserDto userDto = new UserDto();
            userDto.setKakaoId(rootNode.get("id").toString());
            JsonNode propertiesNode = rootNode.get("properties");
            userDto.setName(propertiesNode.get("nickname").asText());

            return userDto;

        } catch (Exception e) {
            System.err.println("Error parsing Kakao API response: " + e.getMessage());
            throw new RuntimeException("Failed to parse Kakao user info", e);
        }
    }
}

