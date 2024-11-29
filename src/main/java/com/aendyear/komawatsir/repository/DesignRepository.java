package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.Design;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignRepository extends JpaRepository<Design, Integer> {

    Optional<Design> findByUserIdAndYear(Integer userId, String year);

}
