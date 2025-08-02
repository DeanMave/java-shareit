package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;
import ru.practicum.shareit.item.dto.ItemSimpleDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final Long userId = 1L;
    private final Long itemId = 1L;

    @Test
    void addItem_shouldReturnOkAndAddedItem() throws Exception {
        ItemOwnerViewDto inputDto = new ItemOwnerViewDto(null, "Дрель", "Простая дрель", true, null, null, null);
        ItemOwnerViewDto outputDto = new ItemOwnerViewDto(itemId, "Дрель", "Простая дрель", true, null, null, null);

        when(itemService.addItem(anyLong(), any(ItemOwnerViewDto.class)))
                .thenReturn(outputDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void addComment_shouldReturnOkAndAddedComment() throws Exception {
        CommentDto inputDto = new CommentDto(null, "Отличная вещь!", null, null);
        CommentDto outputDto = new CommentDto(1L, "Отличная вещь!", "Пользователь", LocalDateTime.now());

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(outputDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Отличная вещь!"));
    }

    @Test
    void updateItem_shouldReturnOkAndUpdatedItem() throws Exception {
        ItemOwnerViewDto inputDto = new ItemOwnerViewDto(null, "Дрель обновленная", null, null, null, null, null);
        ItemOwnerViewDto outputDto = new ItemOwnerViewDto(itemId, "Дрель обновленная", "Простая дрель", true, null, null, null);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemOwnerViewDto.class)))
                .thenReturn(outputDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель обновленная"));
    }

    @Test
    void getItemById_shouldReturnOkAndItemDetails() throws Exception {
        ItemDetailsDto outputDto = new ItemDetailsDto(itemId, "Дрель", "Простая дрель", true, null, null, Collections.emptyList());

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(outputDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void getItemsByOwner_shouldReturnOkAndListOfItems() throws Exception {
        ItemOwnerViewDto item1 = new ItemOwnerViewDto(1L, "Дрель", "Простая дрель", true, null, null, null);
        ItemOwnerViewDto item2 = new ItemOwnerViewDto(2L, "Молоток", "Молоток", true, null, null, null);

        when(itemService.getAllItemsByOwner(anyLong()))
                .thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void searchItems_shouldReturnOkAndListOfItems() throws Exception {
        String searchText = "дрель";
        ItemSimpleDto item = new ItemSimpleDto(itemId, "Дрель", "Простая дрель", true);

        when(itemService.searchItems(anyString()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }
}