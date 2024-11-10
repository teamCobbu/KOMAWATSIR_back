package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
