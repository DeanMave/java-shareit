package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);

    void deleteUserById(Long id);

    UserDto addNewUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    List<UserDto> getAllUsers();
}
