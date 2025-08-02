package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ItemRequestMapperTest {

    @Autowired
    private ItemRequestMapper mapper;

    @Test
    void testToItemRequestDtoOut_shouldMapAllFields() {
        User requestor = new User(1L, "Requestor Name", "requestor@mail.ru");
        ItemRequest itemRequest = new ItemRequest(2L, "Need a screwdriver", requestor, LocalDateTime.now());
        List<ItemRequestResponseDto> items = Collections.singletonList(
                new ItemRequestResponseDto(3L, "Screwdriver", 4L, 2L)
        );
        ItemRequestDtoOut dto = mapper.toItemRequestDtoOut(itemRequest, items);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(itemRequest.getId());
        assertThat(dto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(dto.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(items.get(0).getId());
    }

    @Test
    void testToItemRequest_shouldMapAllFieldsAndIgnoreIdAndCreated() {
        User requestor = new User(1L, "Requestor Name", "requestor@mail.ru");
        ItemRequestDtoIn requestDtoIn = new ItemRequestDtoIn("Need a hammer");
        ItemRequest itemRequest = mapper.toItemRequest(requestDtoIn, requestor);
        assertThat(itemRequest).isNotNull();
        assertThat(itemRequest.getId()).isNull();
        assertThat(itemRequest.getDescription()).isEqualTo(requestDtoIn.getDescription());
        assertThat(itemRequest.getRequestor()).isEqualTo(requestor);
        assertThat(itemRequest.getCreated()).isNull();
    }
}