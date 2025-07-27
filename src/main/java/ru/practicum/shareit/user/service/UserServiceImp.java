package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {

    private final UserRepository repository;

    @Override
    public User getUserById(Long id) {
        log.info("Запрос поиска пользователя по ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return repository.findAll();
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Попытка удаления пользователя по ID: {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден для удаления.");
        }
        repository.deleteById(id);
        log.info("Пользователь с ID {} удален.", id);
    }

    @Override
    @Transactional
    public User addNewUser(User user) {
        log.info("Попытка добавления нового пользователя: {}", user.getEmail());
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new ConflictException("Пользователь с email " + user.getEmail() + " уже существует.");
        }
        User savedUser = repository.save(user);
        log.info("Пользователь добавлен: {}", savedUser);
        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserDto userDto) {
        log.info("Попытка обновления пользователя с ID {}: {}", userId, userDto);
        User existingUser = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден для обновления"));
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            Optional<User> userWithSameEmail = repository.findByEmail(userDto.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(userId)) {
                throw new ConflictException("Email " + userDto.getEmail() + " уже занят другим пользователем.");
            }
        }
        UserMapper.updateFromDto(existingUser, userDto);
        User updatedUser = repository.save(existingUser);
        log.info("Пользователь обновлен: {}", updatedUser);
        return updatedUser;
    }

}