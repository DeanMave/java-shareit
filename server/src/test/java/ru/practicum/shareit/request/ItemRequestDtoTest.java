package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDtoIn> jsonIn;
    @Autowired
    private JacksonTester<ItemRequestDtoOut> jsonOut;

    private final LocalDateTime now = LocalDateTime.now();
    private final UserShortDto requestor = new UserShortDto(1L, null);
    private final List<ItemRequestResponseDto> items = List.of(
            new ItemRequestResponseDto(1L, "Дрель", 100L, 1L)
    );

    @Test
    void testItemRequestDtoInSerialization() throws IOException {
        ItemRequestDtoIn dto = new ItemRequestDtoIn("Нужна дрель");
        JsonContent<ItemRequestDtoIn> result = jsonIn.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
    }

    @Test
    void testItemRequestDtoInDeserialization() throws IOException {
        String json = "{\"description\": \"Нужна дрель\"}";
        ItemRequestDtoIn dto = jsonIn.parse(json).getObject();

        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void testItemRequestDtoOutSerialization() throws IOException {
        ItemRequestDtoOut dto = new ItemRequestDtoOut(1L, "Нужна дрель", requestor, now, items);
        JsonContent<ItemRequestDtoOut> result = jsonOut.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
    }

    @Test
    void testItemRequestDtoOutDeserialization() throws IOException {
        String json = String.format("{\"id\": 1, \"description\": \"Нужна дрель\", \"requestor\": {\"id\": 1}, \"created\": \"%s\", \"items\": []}", now);
        ItemRequestDtoOut dto = jsonOut.parse(json).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getCreated()).isNotNull();
        assertThat(dto.getItems()).isEmpty();
    }
}
