package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.Draft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DraftRepository extends JpaRepository<Draft, Integer> {

    List<Draft> findByUserId(Integer userId);
}
