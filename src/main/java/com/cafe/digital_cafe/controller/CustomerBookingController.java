package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.BookingResponse;
import com.cafe.digital_cafe.dto.CreateBookingRequest;
import com.cafe.digital_cafe.service.CustomerBookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CustomerBookingController {

    private final CustomerBookingService bookingService;

    public CustomerBookingController(CustomerBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        List<BookingResponse> list = bookingService.getMyBookings();
        return ResponseEntity.ok(list);
    }
}
