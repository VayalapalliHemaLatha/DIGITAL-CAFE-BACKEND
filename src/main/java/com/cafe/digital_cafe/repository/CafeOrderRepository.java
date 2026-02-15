package com.cafe.digital_cafe.repository;

import com.cafe.digital_cafe.entity.CafeOrder;
import com.cafe.digital_cafe.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CafeOrderRepository extends JpaRepository<CafeOrder, Long> {

    List<CafeOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<CafeOrder> findByCafeIdOrderByCreatedAtDesc(Long cafeId);

    List<CafeOrder> findByCafeIdAndStatusOrderByCreatedAtAsc(Long cafeId, OrderStatus status);

    List<CafeOrder> findByCafeIdAndStatusInOrderByCreatedAtAsc(Long cafeId, List<OrderStatus> statuses);

    List<CafeOrder> findByOrderDateBetweenOrderByOrderDateAsc(LocalDate start, LocalDate end);

    long countByStatus(OrderStatus status);

    long countByOrderDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM CafeOrder o WHERE o.orderDate BETWEEN :start AND :end")
    BigDecimal sumTotalAmountByOrderDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM CafeOrder o")
    BigDecimal sumTotalAmount();

    long countByStatusAndOrderDateBetween(OrderStatus status, LocalDate start, LocalDate end);
}
