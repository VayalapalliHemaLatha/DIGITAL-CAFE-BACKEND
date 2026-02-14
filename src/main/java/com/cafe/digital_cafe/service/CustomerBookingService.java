package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.BookingResponse;
import com.cafe.digital_cafe.dto.CreateBookingRequest;
import com.cafe.digital_cafe.entity.*;
import com.cafe.digital_cafe.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerBookingService {

    private final TableBookingRepository bookingRepository;
    private final CafeRepository cafeRepository;
    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;

    public CustomerBookingService(TableBookingRepository bookingRepository, CafeRepository cafeRepository,
                                  RestaurantTableRepository tableRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.cafeRepository = cafeRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    public BookingResponse createBooking(CreateBookingRequest request) {
        Long userId = getCurrentUserId();
        Cafe cafe = cafeRepository.findById(request.getCafeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cafe not found"));
        RestaurantTable table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));
        if (!table.getCafeId().equals(request.getCafeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table does not belong to this cafe");
        }
        TableBooking b = new TableBooking();
        b.setUserId(userId);
        b.setCafeId(request.getCafeId());
        b.setTableId(request.getTableId());
        b.setBookingDate(request.getBookingDate());
        b.setBookingTime(request.getBookingTime());
        b.setStatus(BookingStatus.BOOKED);
        b = bookingRepository.save(b);
        User user = userRepository.findById(userId).orElse(null);
        return toResponse(b, cafe.getName(), table.getTableNumber(), user != null ? user.getName() : null);
    }

    public List<BookingResponse> getMyBookings() {
        Long userId = getCurrentUserId();
        return bookingRepository.findByUserIdOrderByBookingDateDescBookingTimeDesc(userId).stream()
                .map(b -> {
                    String cafeName = cafeRepository.findById(b.getCafeId()).map(Cafe::getName).orElse(null);
                    String tableNum = tableRepository.findById(b.getTableId()).map(RestaurantTable::getTableNumber).orElse(null);
                    return toResponse(b, cafeName, tableNum, null);
                })
                .collect(Collectors.toList());
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"))
                .getId();
    }

    private BookingResponse toResponse(TableBooking b, String cafeName, String tableNumber, String userName) {
        BookingResponse r = new BookingResponse();
        r.setId(b.getId());
        r.setUserId(b.getUserId());
        r.setUserName(userName);
        r.setCafeId(b.getCafeId());
        r.setCafeName(cafeName);
        r.setTableId(b.getTableId());
        r.setTableNumber(tableNumber);
        r.setBookingDate(b.getBookingDate());
        r.setBookingTime(b.getBookingTime());
        r.setStatus(b.getStatus());
        return r;
    }
}
