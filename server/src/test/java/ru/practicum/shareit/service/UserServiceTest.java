package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import ru.practicum.shareit.authentication.config.JWTFilter;
import ru.practicum.shareit.authentication.controller.AuthController;
import ru.practicum.shareit.authentication.service.AuthenticationService;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.utils.JWTUtil;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("test")
public class UserServiceTest {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JWTFilter jwtFilter;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private AuthController authController;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User authenticatedUser;

    @Test
    void createDuplicateEmail() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();

        when(userRepository.save(any()))
                .thenThrow(AlreadyExistException.class);
    }

    @Test
    void save() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();

        when(userRepository.save(any()))
                .thenReturn(UserMapper.userDtoToUser(user));

        authenticatedUser = UserMapper.userDtoToUser(user);

        assertThat(authenticatedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void patchNotFoundUser() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> userService.updateUser(1L, user));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void emailWithoutChanges() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(User.builder()
                        .id(1L)
                        .name("test")
                        .email("test@test.com")
                        .build()));

        UserDto result = userService.updateUser(1L, user);
        assertThat(result).usingRecursiveComparison().isEqualTo(user);
        verify(userRepository, times(2)).findById(anyLong());
    }

    @Test
    void patchAllFields() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(User.builder()
                        .id(1L)
                        .name("first")
                        .email("first@test.com")
                        .build()));
        when(userRepository.save(any()))
                .thenReturn(UserMapper.userDtoToUser(user));

        UserDto result = userService.updateUser(1L, user);
        assertThat(result).usingRecursiveComparison().isEqualTo(user);
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void findAll() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();

        when(userRepository.findAll())
                .thenReturn(List.of(UserMapper.userDtoToUser(user)));

        List<UserDto> result = userService.getAllUsers();
        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(user));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getById() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(UserMapper.userDtoToUser(user)));

        UserDto result = userService.getUserById(1L);
        assertThat(result).usingRecursiveComparison().isEqualTo(user);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getByIdNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteById() {
        userService.removeUserById(1L);
        verify(userRepository, times(1)).deleteById(anyLong());
    }

}
