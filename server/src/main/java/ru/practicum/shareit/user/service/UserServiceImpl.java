package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.authentication.security.PersonDetails;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userDto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public User getAuthenticatedUser() {
        PersonDetails principal = (PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userRepository.findByEmail(principal.getUsername());
        if (!user.isPresent()) {
            throw new NotOwnerException("не пользователь");
        }

        return user.get();
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(s);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Пользователь с email = " + s + " не найден");
        }

        return new PersonDetails(user.get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("все пользователи получены");
        return userRepository.findAll().stream()
                .map(UserMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {
        log.debug("Пользователь обновлен");

        User user = getAuthenticatedUser();

        if (userDto.getEmail() != null && userDto.getEmail().equals(user.getEmail())) {
            return getUserById(user.getId());
        }
        userDto.setId(user.getId());

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(userDto.getPassword());
        }

        return UserMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.debug("Пользователь с id = {} получен", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        return UserMapper.userToUserDto(user);
    }

    @Override
    @Transactional
    public void removeUserById(Long userId) {
        log.debug("Пользователь удален");
        userRepository.deleteById(userId);
    }

    @Override
    public void removeYourSelfProfile() {
        User user = getAuthenticatedUser();
        userRepository.deleteById(user.getId());
    }
}
