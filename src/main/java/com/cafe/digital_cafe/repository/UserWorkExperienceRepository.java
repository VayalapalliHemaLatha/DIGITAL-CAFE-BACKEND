package com.cafe.digital_cafe.repository;

import com.cafe.digital_cafe.entity.UserWorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWorkExperienceRepository extends JpaRepository<UserWorkExperience, Long> {

    List<UserWorkExperience> findByUserIdOrderByIdAsc(Long userId);

    void deleteByUserId(Long userId);
}
