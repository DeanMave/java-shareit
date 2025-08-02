package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BookingMapperTest {

    @Autowired
    private BookingMapper mapper;

    @Test
    void testToBookingResponseDto_shouldMapAllFields() {
        User booker = new User(1L, "Booker Name", "booker@mail.ru");
        User owner = new User(2L, "Owner Name", "owner@mail.ru");
        Item item = new Item(3L, "Drill", "Powerful drill", owner, true, null);
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        Booking booking = new Booking(4L, start, end, item, booker, StatusBooking.APPROVED);
        BookingResponseDto dto = mapper.toBookingResponseDto(booking);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(booking.getId());
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(dto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(dto.getItem().getId()).isEqualTo(item.getId());
        assertThat(dto.getItem().getName()).isEqualTo(item.getName());
        assertThat(dto.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(dto.getBooker().getName()).isEqualTo(booker.getName());
    }

    @Test
    void testToBooking_shouldSetStatusToWaitingAndIgnoreId() {
        User booker = new User(1L, "Booker Name", "booker@mail.ru");
        User owner = new User(2L, "Owner Name", "owner@mail.ru");
        Item item = new Item(3L, "Drill", "Powerful drill", owner, true, null);
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingRequestDto requestDto = new BookingRequestDto(item.getId(), start, end);
        Booking booking = mapper.toBooking(requestDto, item, booker);
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isNull();
        assertThat(booking.getStatus()).isEqualTo(StatusBooking.WAITING);
        assertThat(booking.getStart()).isEqualTo(start);
        assertThat(booking.getEnd()).isEqualTo(end);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
    }

    @Test
    void testToBookingItemDto_shouldMapBookerIdCorrectly() {
        User booker = new User(1L, "Booker Name", "booker@mail.ru");
        User owner = new User(2L, "Owner Name", "owner@mail.ru");
        Item item = new Item(3L, "Drill", "Powerful drill", owner, true, null);
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        Booking booking = new Booking(4L, start, end, item, booker, StatusBooking.APPROVED);
        BookingItemDto dto = mapper.toBookingItemDto(booking);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(booking.getId());
        assertThat(dto.getBookerId()).isEqualTo(booking.getBooker().getId());
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
    }
}