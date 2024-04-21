package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.userDto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    UserDto getUserById(Long userId);

    void removeUserById(Long userId);

}
