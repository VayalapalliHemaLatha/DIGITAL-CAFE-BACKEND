package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.BookingResponse;
import com.cafe.digital_cafe.service.CafeOwnerBookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafeowners/bookings")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CafeOwnerBookingController {

    private final CafeOwnerBookingService cafeOwnerBookingService;

    public CafeOwnerBookingController(CafeOwnerBookingService cafeOwnerBookingService) {
        this.cafeOwnerBookingService = cafeOwnerBookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getBookings() {
        List<BookingResponse> list = cafeOwnerBookingService.getBookings();
        return ResponseEntity.ok(list);
    }
}
