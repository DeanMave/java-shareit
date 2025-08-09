package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.enums.StatusBooking;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

    @JsonTest
    public class BookingDtoTest {

        @Autowired
        private JacksonTester<BookingRequestDto> jsonRequestDto;
        @Autowired
        private JacksonTester<BookingResponseDto> jsonResponseDto;
        @Autowired
        private JacksonTester<BookingItemDto> jsonItemDto;

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

        @Test
        void testBookingRequestDtoSerialization() throws IOException {
            LocalDateTime start = LocalDateTime.now().plusHours(1);
            LocalDateTime end = LocalDateTime.now().plusHours(2);
            BookingRequestDto dto = new BookingRequestDto(1L, start, end);

            JsonContent<BookingRequestDto> result = jsonRequestDto.write(dto);

            BookingRequestDto parsedDto = jsonRequestDto.parse(result.getJson()).getObject();

            assertThat(parsedDto.getItemId()).isEqualTo(dto.getItemId());
            assertThat(parsedDto.getStart()).isEqualTo(dto.getStart());
            assertThat(parsedDto.getEnd()).isEqualTo(dto.getEnd());
        }

        @Test
        void testBookingRequestDtoDeserialization() throws IOException {
            LocalDateTime expectedStart = LocalDateTime.now().plusDays(1);
            LocalDateTime expectedEnd = LocalDateTime.now().plusDays(2);
            String jsonContent = "{\"itemId\": 2, \"start\": \"" + expectedStart.format(FORMATTER) + "\", \"end\": \"" + expectedEnd.format(FORMATTER) + "\"}";

            BookingRequestDto dto = jsonRequestDto.parse(jsonContent).getObject();

            assertThat(dto.getItemId()).isEqualTo(2L);
             assertThat(dto.getStart()).isNotNull();
             assertThat(dto.getEnd()).isNotNull();
        }

        @Test
        void testBookingResponseDtoSerialization() throws IOException {
            LocalDateTime start = LocalDateTime.now().plusHours(1);
            LocalDateTime end = LocalDateTime.now().plusHours(2);
            ItemShortDto itemShortDto = new ItemShortDto(10L, "Test Item");
            UserShortDto bookerShortDto = new UserShortDto(20L, "Test Booker");

            BookingResponseDto dto = new BookingResponseDto(
                    1L,
                    start,
                    end,
                    StatusBooking.APPROVED,
                    itemShortDto,
                    bookerShortDto
            );

            JsonContent<BookingResponseDto> result = jsonResponseDto.write(dto);

            BookingResponseDto parsedDto = jsonResponseDto.parse(result.getJson()).getObject();

            assertThat(parsedDto.getId()).isEqualTo(dto.getId());
            assertThat(parsedDto.getStart()).isEqualTo(dto.getStart());
            assertThat(parsedDto.getEnd()).isEqualTo(dto.getEnd());
            assertThat(parsedDto.getStatus()).isEqualTo(dto.getStatus());
            assertThat(parsedDto.getItem()).isEqualTo(dto.getItem());
            assertThat(parsedDto.getBooker()).isEqualTo(dto.getBooker());
        }

        @Test
        void testBookingItemDtoSerialization() throws IOException {
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = LocalDateTime.now().plusHours(1);
            BookingItemDto dto = new BookingItemDto(1L, 100L, start, end);

            JsonContent<BookingItemDto> result = jsonItemDto.write(dto);

            BookingItemDto parsedDto = jsonItemDto.parse(result.getJson()).getObject();

            assertThat(parsedDto.getId()).isEqualTo(dto.getId());
            assertThat(parsedDto.getBookerId()).isEqualTo(dto.getBookerId());
            assertThat(parsedDto.getStart()).isEqualTo(dto.getStart());
            assertThat(parsedDto.getEnd()).isEqualTo(dto.getEnd());
        }

        @Test
        void testBookingItemDtoDeserialization() throws IOException {
            LocalDateTime expectedStart = LocalDateTime.now();
            LocalDateTime expectedEnd = LocalDateTime.now().plusHours(1);
            String jsonContent = "{\"id\": 1, \"bookerId\": 100, \"start\": \"" + expectedStart.format(FORMATTER) + "\", \"end\": \"" + expectedEnd.format(FORMATTER) + "\"}";
            BookingItemDto dto = jsonItemDto.parse(jsonContent).getObject();

            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getBookerId()).isEqualTo(100L);
            assertThat(dto.getStart()).isEqualTo(expectedStart);
            assertThat(dto.getEnd()).isEqualTo(expectedEnd);
        }
}
