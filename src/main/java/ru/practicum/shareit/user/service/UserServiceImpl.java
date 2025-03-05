package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.interfaces.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private long id = 0;
    private final List<String> emails = new ArrayList<>();

    @Override
    public UserDto createUser(UserDto userDto) {
        if (existsByEmail(userDto.getEmail())) {
            throw new ValidationException("Такой емаил уже существует"); // Email уже существует
        }
        User user = UserMapper.toUser(userDto);
        emails.add(user.getEmail());
        return UserMapper.toUserDto(userRepository.createUser(generateId(), user));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto patchUser(long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new ValidationException("Email не может быть таким же");
            }
            if (existsByEmail(userDto.getEmail())) {
                throw new ValidationException("Email должен быть уникальным");
            }
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.toUserDto(userRepository.getUser(id));
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
    }

    private long generateId() {
        return id++;
    }

    private boolean existsByEmail(String email) {
        return emails.contains(email);
    }
}
