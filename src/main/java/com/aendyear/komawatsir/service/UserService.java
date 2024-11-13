package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.auth.KakaoAuthService;
import com.aendyear.komawatsir.auth.KakaoUser;
import com.aendyear.komawatsir.auth.KakaoUserService;
import com.aendyear.komawatsir.entity.User;
import com.aendyear.komawatsir.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UserService {

    private final KakaoAuthService kakaoAuthService;
    private final KakaoUserService kakaoUserService;
    private final UserRepository userRepository;

    @Autowired
    public UserService(KakaoAuthService kakaoAuthService, KakaoUserService kakaoUserService, UserRepository userRepository) {
        this.kakaoAuthService = kakaoAuthService;
        this.kakaoUserService = kakaoUserService;
        this.userRepository = userRepository;
    }

    public User processKakaoLogin(String code, String clientId, String redirectUri) {
        // 카카오 API 호출 요청
        String accessToken = kakaoAuthService.getAccessToken(code, clientId, redirectUri);
        // Access Token 가져오기, 사용자 정보 가져오기
        KakaoUser kakaoUser = kakaoUserService.getKakaoUserInfo(accessToken);

        // 사용자 정보 유무 확인
        User existingUser = userRepository.findByKakaoId(kakaoUser.getNickname());
        if (existingUser != null) {
            return existingUser;
        }
        // 새로운 사용자 정보를 DB에 저장
        User newUser = User.builder()
//                .name(kakaoUser.getName())
                .kakaoId(kakaoUser.getNickname())
//                .tel(kakaoUser.getTel())  // 사용자 정보에 맞게 추가
//                .isSmsAllowed(true)  // 기본값 설정, 실제로는 요구사항에 맞게 설정
                .build();
        return userRepository.save(newUser);
    }
}
