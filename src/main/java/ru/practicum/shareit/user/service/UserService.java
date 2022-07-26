package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.models.User;
import ru.practicum.shareit.user.models.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage userStorage;

    private final UserMapper userMapper;

    public List<UserDto> getAll() {
        return userMapper.toListDto(userStorage.findAll());
    }

    public UserDto getUser(long id) {
        return userMapper.toDto(userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"))
        );
    }

    private void validate(UserDto userDto) {
        userStorage.findById(userDto.getId()).ifPresent(user -> {
            throw new ValidationException("User already exists");
        });
        if (userDto.getEmail() == null) {
            throw new ValidationException("Email cant be null");
        }
        if (userDto.getName() == null) {
            throw new ValidationException("Name cant be null");
        }
    }

    public UserDto create(UserDto userDto) {
        validate(userDto);
        userDto.setId(0L);
        return userMapper.toDto(userStorage.save(userMapper.fromDto(userDto)));
    }

    public UserDto update(UserDto userDto, long id) {
        userDto.setId(id);
        UserDto oldUser = getUser(id);
        User user = userMapper.fromDto(oldUser);
        userMapper.updateUserFromDto(userDto, user);
        return userMapper.toDto(userStorage.update(user));
    }

    public UserDto delete(long id) {
        return userMapper.toDto(userStorage.deleteById(id));
    }
}
