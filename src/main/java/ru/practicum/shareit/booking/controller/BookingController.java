package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<BookingResponseDto> getUserBookings(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@Positive Long ownerId,
                                                     @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(ownerId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Positive @PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingResponseDto addNewBooking(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody BookingRequestDto requestDto) {
        return bookingService.addNewBooking(requestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Positive @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        return bookingService.updateBooking(bookingId, userId, approved);
    }
}
