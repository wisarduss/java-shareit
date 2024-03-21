package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.IdNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        checkDuplicateEmail(user.getEmail());
        user.setId(generatorId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User updateUser = users.get(userId);

        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (!updateUser.getEmail().equals(user.getEmail())) {
                checkDuplicateEmail(user.getEmail());
                updateUser.setEmail(user.getEmail());
            }
        }
        users.put(userId, updateUser);

        return updateUser;
    }

    @Override
    public User getById(Long userId) {
        User user = users.get(userId);

        if (user == null) {
            throw new IdNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return user;
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(userId);
    }

    private Long generatorId() {
        return ++id;
    }

    private void checkDuplicateEmail(String email) {
        List<String> emailsList = users.values().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        if (emailsList.contains(email)) {
            throw new AlreadyExistException("Пользователь с таким email уже существует");
        }
    }
}
