package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> jsonUser;
    @Autowired
    private JacksonTester<UserShortDto> jsonUserShort;

    @Test
    void testUserDtoSerialization() throws IOException {
        UserDto dto = new UserDto(1L, "Test User", "test@mail.ru");
        JsonContent<UserDto> result = jsonUser.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@mail.ru");
    }

    @Test
    void testUserDtoDeserialization() throws IOException {
        String json = "{\"id\": 1, \"name\": \"Test User\", \"email\": \"test@mail.ru\"}";
        UserDto dto = jsonUser.parse(json).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@mail.ru");
    }

    @Test
    void testUserShortDtoSerialization() throws IOException {
        UserShortDto dto = new UserShortDto(1L, "Test User");
        JsonContent<UserShortDto> result = jsonUserShort.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
    }

    @Test
    void testUserShortDtoDeserialization() throws IOException {
        String json = "{\"id\": 1, \"name\": \"Test User\"}";
        UserShortDto dto = jsonUserShort.parse(json).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
    }
}