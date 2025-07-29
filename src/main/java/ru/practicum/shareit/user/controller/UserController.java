package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@Positive @PathVariable Long id) {
        return service.getUserById(id);
    }

    @PostMapping
    public UserDto addNewUser(@Valid @RequestBody UserDto userDto) {
        return service.addNewUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Positive @PathVariable Long id, @RequestBody UserDto userDto) {
        return service.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Positive @PathVariable Long id) {
        service.deleteUserById(id);
    }
}
