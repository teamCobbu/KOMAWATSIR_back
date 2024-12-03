package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.Image;
import com.aendyear.komawatsir.repository.dsl.ImageRepositoryDSL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer>, ImageRepositoryDSL {


}
