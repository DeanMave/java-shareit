package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper mapper;

    @Test
    void testToUserDto_shouldMapAllFields() {
        User user = new User(1L, "Test User", "test@mail.ru");

        UserDto dto = mapper.toUserDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getName()).isEqualTo(user.getName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void testToUser_shouldMapAllFieldsAndIgnoreId() {
        UserDto dto = new UserDto(1L, "New User", "new@mail.ru");

        User user = mapper.toUser(dto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    void testUpdateFromDto_shouldUpdateNonNullFields() {
        User user = new User(1L, "Old Name", "old@mail.ru");
        UserDto updateDto = new UserDto(null, "Updated Name", null);

        mapper.updateFromDto(user, updateDto);

        assertThat(user.getName()).isEqualTo("Updated Name");
        assertThat(user.getEmail()).isEqualTo("old@mail.ru");
    }

    @Test
    void testUpdateFromDto_withNullDto_shouldNotChangeUser() {
        User user = new User(1L, "Old Name", "old@mail.ru");
        UserDto updateDto = new UserDto();

        mapper.updateFromDto(user, updateDto);

        assertThat(user.getName()).isEqualTo("Old Name");
        assertThat(user.getEmail()).isEqualTo("old@mail.ru");
    }

    @Test
    void testToUserShortDto_shouldMapIdAndName() {
        User user = new User(1L, "Test User", "test@mail.ru");

        UserShortDto dto = mapper.toUserShortDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getName()).isEqualTo(user.getName());
    }
}