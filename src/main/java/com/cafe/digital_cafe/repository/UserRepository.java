package com.cafe.digital_cafe.repository;

import com.cafe.digital_cafe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cafe.digital_cafe.entity.RoleType;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleTypeOrderByNameAsc(RoleType roleType);

    List<User> findByRoleTypeAndCafeIdOrderByNameAsc(RoleType roleType, Long cafeId);
}
