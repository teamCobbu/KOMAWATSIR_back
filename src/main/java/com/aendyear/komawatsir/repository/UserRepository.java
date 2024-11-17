package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByKakaoId(String kakaoId);
    Optional<User> findByTel(String tel); //비회원 검색
}
