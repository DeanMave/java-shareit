package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return service.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@Positive @PathVariable Long id) {
        return UserMapper.toUserDto(service.getUserById(id));
    }

    @PostMapping
    public UserDto addNewUser(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(service.addNewUser(user));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Positive @PathVariable Long id, @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(service.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Positive @PathVariable Long id) {
        service.deleteUserById(id);
    }
}
