package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.userDto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("все пользователи получены");
        List<User> users = userRepository.getAll();

        return users.stream()
                .map(this::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.debug("Пользователь создан");
        User user = userDtoToUser(userDto);
        User addUser = userRepository.create(user);
        return userToUserDto(addUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        log.debug("Пользователь обновлен");
        User user = userDtoToUser(userDto);
        User updateUser = userRepository.update(userId, user);
        return userToUserDto(updateUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.debug("Пользователь с id = {} получен", userId);
        User user = userRepository.getById(userId);
        return userToUserDto(user);
    }

    @Override
    public void removeUserById(Long userId) {
        log.debug("Пользователь удален");
        userRepository.deleteById(userId);
    }

    private User userDtoToUser(UserDto userDto) {

        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    private UserDto userToUserDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
