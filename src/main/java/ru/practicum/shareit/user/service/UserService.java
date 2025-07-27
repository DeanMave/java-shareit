package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);

    void deleteUserById(Long id);

    User addNewUser(User user);

    User updateUser(Long userId, UserDto userDto);

    List<User> getAllUsers();
}
