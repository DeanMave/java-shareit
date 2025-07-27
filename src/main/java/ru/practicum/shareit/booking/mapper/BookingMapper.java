package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.enums.StatusBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                ItemMapper.toItemShortDto(booking.getItem()),
                UserMapper.toUserShortDto(booking.getBooker())
        );
    }

    public static Booking toBooking(BookingRequestDto bookingDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusBooking.WAITING);
        return booking;
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingItemDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

}
