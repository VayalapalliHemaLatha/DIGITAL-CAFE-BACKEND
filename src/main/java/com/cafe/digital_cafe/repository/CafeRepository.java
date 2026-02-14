package com.cafe.digital_cafe.repository;

import com.cafe.digital_cafe.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CafeRepository extends JpaRepository<Cafe, Long> {

    List<Cafe> findAllByOrderByNameAsc();
}
