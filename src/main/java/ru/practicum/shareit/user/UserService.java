package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(Long userId, User user);

    User getUserById(Long userId);

    void removeUserById(Long userId);

}
