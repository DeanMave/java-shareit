package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.item.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService service;

    private final Long userId = 1L;
    private final Long requestId = 1L;
    private final UserShortDto userShortDto = new UserShortDto(userId,null);
    private final List<ItemRequestResponseDto> items = List.of(
            new ItemRequestResponseDto(1L, "Дрель", 100L, requestId)
    );

    @Test
    void addNewRequest_shouldReturnOkAndCreatedRequest() throws Exception {
        ItemRequestDtoIn inputDto = new ItemRequestDtoIn("Нужна дрель");
        ItemRequestDtoOut outputDto = new ItemRequestDtoOut(requestId, "Нужна дрель", userShortDto, LocalDateTime.now(), items);

        when(service.addNewRequest(anyLong(), any(ItemRequestDtoIn.class)))
                .thenReturn(outputDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void getUserRequests_shouldReturnOkAndListOfRequests() throws Exception {
        ItemRequestDtoOut requestDto = new ItemRequestDtoOut(requestId, "Нужна дрель", userShortDto, LocalDateTime.now(), items);

        when(service.getUserRequests(anyLong()))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(requestId));
    }

    @Test
    void getAllRequests_shouldReturnOkAndListOfRequests() throws Exception {
        ItemRequestDtoOut requestDto = new ItemRequestDtoOut(requestId, "Нужна дрель", userShortDto, LocalDateTime.now(), items);

        when(service.getAllRequests(anyLong(), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(requestId));
    }

    @Test
    void getRequestById_shouldReturnOkAndRequest() throws Exception {
        ItemRequestDtoOut requestDto = new ItemRequestDtoOut(requestId, "Нужна дрель", userShortDto, LocalDateTime.now(), items);

        when(service.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }
}