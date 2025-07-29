package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto getBookingById(Long bookingId, Long userId);

    BookingResponseDto addNewBooking(BookingRequestDto requestDto, Long userId);

    BookingResponseDto updateBooking(Long bookingId, Long ownerId, Boolean approved);

    List<BookingResponseDto> getUserBookings(Long bookerId, String state);

    List<BookingResponseDto> getOwnerBookings(Long ownerId, String state);
}

