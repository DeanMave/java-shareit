package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName(),
                user.getEmail());
    }

    public static User toUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return new User(null, userDto.getName(), userDto.getEmail());
    }

    public static User updateFromDto(User user, UserDto userDto) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }

    public static UserShortDto toUserShortDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserShortDto(user.getId(), user.getName());
    }
}
