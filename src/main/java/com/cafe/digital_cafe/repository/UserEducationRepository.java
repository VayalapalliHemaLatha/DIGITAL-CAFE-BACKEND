package com.cafe.digital_cafe.repository;

import com.cafe.digital_cafe.entity.UserEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserEducationRepository extends JpaRepository<UserEducation, Long> {

    List<UserEducation> findByUserIdOrderByIdAsc(Long userId);

    void deleteByUserId(Long userId);
}
