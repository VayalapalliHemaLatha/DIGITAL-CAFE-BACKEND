package com.cafe.digital_cafe.repository;

import com.cafe.digital_cafe.entity.RestaurantTable;
import com.cafe.digital_cafe.entity.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    List<RestaurantTable> findAllByOrderByTableNumberAsc();

    long countByStatus(TableStatus status);

    boolean existsByTableNumber(String tableNumber);

    List<RestaurantTable> findByCafeIdOrderByTableNumberAsc(Long cafeId);

    long countByCafeIdAndStatus(Long cafeId, TableStatus status);

    boolean existsByCafeIdAndTableNumber(Long cafeId, String tableNumber);
}
