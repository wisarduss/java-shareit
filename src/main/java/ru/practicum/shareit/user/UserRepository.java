package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    List<User> getAll();

    User create(User user);

    User update(Long userId, User user);

    User getById(Long userId);

    void deleteById(Long userId);

}
