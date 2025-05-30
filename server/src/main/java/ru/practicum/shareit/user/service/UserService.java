package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.userDto.UserDto;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User getAuthenticatedUser();

    List<UserDto> getAllUsers();

    UserDto updateUser(UserDto userDto);

    UserDto getUserById(Long userId);

    void removeUserById(Long userId);

    void removeYourSelfProfile();

}
