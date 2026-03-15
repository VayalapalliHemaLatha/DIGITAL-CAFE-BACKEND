package com.cafe.digital_cafe.repository;

import com.cafe.digital_cafe.entity.CafeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<CafeOrder, Long> {

    List<CafeOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<CafeOrder> findAllByOrderByCreatedAtDesc();
}
