package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDetailsDto> jsonItemDetails;
    @Autowired
    private JacksonTester<CommentDto> jsonComment;
    @Autowired
    private JacksonTester<ItemOwnerViewDto> jsonItemOwnerView;
    @Autowired
    private JacksonTester<ItemRequestResponseDto> jsonItemRequestResponse;
    @Autowired
    private JacksonTester<ItemShortDto> jsonItemShort;
    @Autowired
    private JacksonTester<ItemSimpleDto> jsonItemSimple;

    private final LocalDateTime now = LocalDateTime.now();
    private final BookingItemDto bookingItemDto = new BookingItemDto(1L, 1L, now, now.plusHours(1));
    private final CommentDto commentDto = new CommentDto(1L, "Отличный комментарий!", "Пользователь", now);

    @Test
    void testItemDetailsDtoSerialization() throws IOException {
        ItemDetailsDto dto = new ItemDetailsDto(1L, "Дрель", "Простая дрель", true, bookingItemDto, bookingItemDto, Collections.singletonList(commentDto));

        JsonContent<ItemDetailsDto> result = jsonItemDetails.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Простая дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
    }

    @Test
    void testItemDetailsDtoDeserialization() throws IOException {
        String json = "{\"id\": 1, \"name\": \"Дрель\", \"description\": \"Простая дрель\", \"available\": true}";
        ItemDetailsDto dto = jsonItemDetails.parse(json).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Простая дрель");
        assertThat(dto.getAvailable()).isEqualTo(true);
    }

    @Test
    void testItemOwnerViewDtoSerialization() throws IOException {
        ItemOwnerViewDto dto = new ItemOwnerViewDto(1L, "Дрель", "Простая дрель", true, 1L, bookingItemDto, bookingItemDto);

        JsonContent<ItemOwnerViewDto> result = jsonItemOwnerView.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
    }

    @Test
    void testItemOwnerViewDtoDeserialization() throws IOException {
        String json = "{\"id\": 1, \"name\": \"Дрель\", \"description\": \"Простая дрель\", \"available\": true, \"requestId\": 1}";
        ItemOwnerViewDto dto = jsonItemOwnerView.parse(json).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getRequestId()).isEqualTo(1L);
    }

    @Test
    void testItemRequestResponseDtoSerialization() throws IOException {
        ItemRequestResponseDto dto = new ItemRequestResponseDto(1L, "Дрель", 100L, 200L);

        JsonContent<ItemRequestResponseDto> result = jsonItemRequestResponse.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(100);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(200);
    }

    @Test
    void testItemShortDtoSerialization() throws IOException {
        ItemShortDto dto = new ItemShortDto(1L, "Дрель");

        JsonContent<ItemShortDto> result = jsonItemShort.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
    }

    @Test
    void testItemSimpleDtoSerialization() throws IOException {
        ItemSimpleDto dto = new ItemSimpleDto(1L, "Дрель", "Простая дрель", true);

        JsonContent<ItemSimpleDto> result = jsonItemSimple.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void testCommentDtoSerialization() throws IOException {
        CommentDto dto = new CommentDto(1L, "Отличный комментарий!", "Пользователь", now);

        JsonContent<CommentDto> result = jsonComment.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличный комментарий!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Пользователь");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
    }

    @Test
    void testCommentDtoDeserialization() throws IOException {
        String json = String.format("{\"id\": 1, \"text\": \"Отличный комментарий!\", \"authorName\": \"Пользователь\", \"created\": \"%s\"}", now);
        CommentDto dto = jsonComment.parse(json).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Отличный комментарий!");
        assertThat(dto.getAuthorName()).isEqualTo("Пользователь");
        assertThat(dto.getCreated()).isNotNull();
    }
}