package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<BookingResponseDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(userId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingResponseDto addNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody BookingRequestDto requestDto) {
        return bookingService.addNewBooking(requestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        return bookingService.updateBooking(bookingId, userId, approved);
    }
}
