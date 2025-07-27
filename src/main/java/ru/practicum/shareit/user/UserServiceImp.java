package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImp implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 1;

    @Override
    public User getUserById(Long id) {
        log.info("Запрос поиска пользователя по ID: {}", id);
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Запрос всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Попытка удаления пользователя по ID: {}", id);
        if (users.remove(id) == null) {
            log.warn("Попытка удалить несуществующего пользователя с ID: {}", id);
        } else {
            log.info("Пользователь с ID {} удален.", id);
        }
    }

    @Override
    public User addNewUser(User user) {
        log.info("Попытка добавления нового пользователя: {}", user.getEmail());
        log.info("Текущие пользователи в системе: {}", users.values().stream()
                .map(u -> "ID: " + u.getId() + " Email: " + u.getEmail())
                .collect(Collectors.joining("; ")));
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("Пользователь с email " + user.getEmail() + " уже существует.");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        log.info("Попытка обновления пользователя с ID {}: {}", userId, userDto);
        User existingUser = users.get(userId);
        if (existingUser == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден для обновления");
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (users.values().stream().anyMatch(u -> !u.getId().equals(userId) && u.getEmail().equals(userDto.getEmail()))) {
                throw new ConflictException("Email " + userDto.getEmail() + " уже занят другим пользователем.");
            }
        }
        UserMapper.updateFromDto(existingUser, userDto);
        users.put(existingUser.getId(), existingUser);
        log.info("Пользователь обновлен: {}", existingUser);
        return existingUser;
    }

    private Long generateId() {
        return currentId++;
    }
}
