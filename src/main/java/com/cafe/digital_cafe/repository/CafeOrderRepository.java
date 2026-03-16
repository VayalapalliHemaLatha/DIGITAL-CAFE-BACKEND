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

    // Queries for CafeOwner Dashboard
    long countByCafeId(Long cafeId);

    long countByCafeIdAndStatus(Long cafeId, OrderStatus status);

    long countByCafeIdAndOrderDateBetween(Long cafeId, LocalDate start, LocalDate end);

    long countByCafeIdAndStatusAndOrderDateBetween(Long cafeId, OrderStatus status, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM CafeOrder o WHERE o.cafeId = :cafeId")
    BigDecimal sumTotalAmountByCafeId(@Param("cafeId") Long cafeId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM CafeOrder o WHERE o.cafeId = :cafeId AND o.orderDate BETWEEN :start AND :end")
    BigDecimal sumTotalAmountByCafeIdAndOrderDateBetween(@Param("cafeId") Long cafeId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    List<CafeOrder> findByCafeIdAndOrderDateBetweenOrderByOrderDateAsc(Long cafeId, LocalDate start, LocalDate end);

    @Query("SELECT COUNT(DISTINCT o.userId) FROM CafeOrder o WHERE o.cafeId = :cafeId")
    long countDistinctUsersByCafeId(@Param("cafeId") Long cafeId);
}
