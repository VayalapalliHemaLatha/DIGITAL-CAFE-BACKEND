package com.cafe.digital_cafe.repository;

import com.cafe.digital_cafe.entity.CafeOrder;
import com.cafe.digital_cafe.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CafeOrderRepository extends JpaRepository<CafeOrder, Long> {

    List<CafeOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<CafeOrder> findByCafeIdOrderByCreatedAtDesc(Long cafeId);

    List<CafeOrder> findByCafeIdAndStatusOrderByCreatedAtAsc(Long cafeId, OrderStatus status);

    List<CafeOrder> findByCafeIdAndStatusInOrderByCreatedAtAsc(Long cafeId, List<OrderStatus> statuses);
}
