package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void getUserBookings_shouldReturnOkAndListOfBookings() throws Exception {
        Long userId = 1L;
        BookingResponseDto booking = new BookingResponseDto();
        booking.setId(1L);

        when(bookingService.getUserBookings(userId, "ALL"))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerBookings_shouldReturnOkAndListOfBookings() throws Exception {
        Long userId = 1L;
        BookingResponseDto booking = new BookingResponseDto();
        booking.setId(1L);
        when(bookingService.getOwnerBookings(userId, "ALL"))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingById_shouldReturnOkAndBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(bookingId);

        when(bookingService.getBookingById(bookingId, userId))
                .thenReturn(responseDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    void addNewBooking_shouldReturnOkAndAddedBooking() throws Exception {
        Long userId = 1L;
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);

        when(bookingService.addNewBooking(eq(userId), any(BookingRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));
    }

    @Test
    void updateBooking_shouldReturnOkAndUpdatedBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(bookingId);

        when(bookingService.updateBooking(bookingId, userId, approved))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

}
