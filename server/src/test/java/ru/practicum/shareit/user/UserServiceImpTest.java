package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceImpTest {

    @Autowired
    private UserService userService;

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        user1 = new UserDto(null, "Test User 1", "test1@mail.ru");
        user2 = new UserDto(null, "Test User 2", "test2@mail.ru");
    }

    @Test
    void addNewUser_shouldSaveAndReturnUser() {
        UserDto savedUser = userService.addNewUser(user1);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    void addNewUser_whenEmailExists_shouldThrowConflictException() {
        userService.addNewUser(user1);

        assertThatThrownBy(() -> userService.addNewUser(user1))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("уже существует.");
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserDto savedUser = userService.addNewUser(user1);
        UserDto foundUser = userService.getUserById(savedUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void getUserById_whenUserNotFound_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        userService.addNewUser(user1);
        userService.addNewUser(user2);
        List<UserDto> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo(user1.getName());
    }

    @Test
    void updateUser_shouldUpdateUser() {
        UserDto savedUser = userService.addNewUser(user1);
        UserDto updateDto = new UserDto(null, "Updated User", "updated@mail.ru");
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertThat(updatedUser.getName()).isEqualTo("Updated User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@mail.ru");
    }

    @Test
    void updateUser_whenNewEmailExists_shouldThrowConflictException() {
        UserDto savedUser1 = userService.addNewUser(user1);
        UserDto savedUser2 = userService.addNewUser(user2);
        UserDto updateDto = new UserDto(null, null, savedUser2.getEmail());

        assertThatThrownBy(() -> userService.updateUser(savedUser1.getId(), updateDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("уже занят другим пользователем.");
    }

    @Test
    void deleteUserById_shouldDeleteUser() {
        UserDto savedUser = userService.addNewUser(user1);
        userService.deleteUserById(savedUser.getId());

        assertThatThrownBy(() -> userService.getUserById(savedUser.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUser_whenUpdateOnlyName_shouldUpdateNameAndKeepEmail() {
        UserDto savedUser = userService.addNewUser(user1);
        UserDto updateDto = new UserDto(null, "Name Only Updated", null);
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertThat(updatedUser.getName()).isEqualTo("Name Only Updated");
        assertThat(updatedUser.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    void updateUser_whenUpdateOnlyEmail_shouldUpdateEmailAndKeepName() {
        UserDto savedUser = userService.addNewUser(user1);
        UserDto updateDto = new UserDto(null, null, "email_only_updated@mail.ru");
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertThat(updatedUser.getName()).isEqualTo(user1.getName());
        assertThat(updatedUser.getEmail()).isEqualTo("email_only_updated@mail.ru");
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowNotFoundException() {
        UserDto updateDto = new UserDto(null, "Updated User", "updated@mail.ru");

        assertThatThrownBy(() -> userService.updateUser(999L, updateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден для обновления");
    }

    @Test
    void deleteUserById_whenUserNotFound_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.deleteUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID 999 не найден для удаления.");
    }
}