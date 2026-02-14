package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.BookingResponse;
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
public class CafeOwnerBookingService {

    private final TableBookingRepository bookingRepository;
    private final CafeRepository cafeRepository;
    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;

    public CafeOwnerBookingService(TableBookingRepository bookingRepository, CafeRepository cafeRepository,
                                   RestaurantTableRepository tableRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.cafeRepository = cafeRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    public List<BookingResponse> getBookings() {
        Long cafeId = getCafeOwnerCafeId();
        return bookingRepository.findByCafeIdOrderByBookingDateDescBookingTimeDesc(cafeId).stream()
                .map(b -> {
                    String cafeName = cafeRepository.findById(b.getCafeId()).map(Cafe::getName).orElse(null);
                    String tableNum = tableRepository.findById(b.getTableId()).map(RestaurantTable::getTableNumber).orElse(null);
                    String userName = userRepository.findById(b.getUserId()).map(User::getName).orElse(null);
                    return toResponse(b, cafeName, tableNum, userName);
                })
                .collect(Collectors.toList());
    }

    private Long getCafeOwnerCafeId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRoleType() != RoleType.CAFE_OWNER || user.getCafeId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cafe owner access required");
        }
        return user.getCafeId();
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
