package ru.practicum.shareit.user.service.interfaces;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getUsers();

    UserDto patchUser(long id, UserDto userDto);

    UserDto getUser(long id);

    void deleteUser(long userId);
}
