package com.cafe.digital_cafe.repository;

import com.cafe.digital_cafe.entity.BookingStatus;
import com.cafe.digital_cafe.entity.TableBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {

    List<TableBooking> findByUserIdOrderByBookingDateDescBookingTimeDesc(Long userId);

    List<TableBooking> findByCafeIdOrderByBookingDateDescBookingTimeDesc(Long cafeId);

    List<TableBooking> findByCafeIdAndBookingDateAndStatus(Long cafeId, LocalDate date, BookingStatus status);
}
