package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        log.debug("все пользователи получены");
        return userRepository.getAll();
    }

    @Override
    public User createUser(User user) {
        log.debug("Пользователь создан");
        return userRepository.create(user);
    }

    @Override
    public User updateUser(Long userId, User user) {
        log.debug("Пользователь обновлен");
        return userRepository.update(userId, user);
    }

    @Override
    public User getUserById(Long userId) {
        log.debug("Пользователь с id = {} получен", userId);
        return userRepository.getById(userId);
    }

    @Override
    public void removeUserById(Long userId) {
        log.debug("Пользователь удален");
        userRepository.deleteById(userId);
    }
}
