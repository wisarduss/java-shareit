package ru.practicum.shareit.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userDto.UserDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    private UserDto userDto;
    private Errors errors;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .password("password123")
                .build();

        errors = new BeanPropertyBindingResult(userDto, "userDto");
    }

    @Test
    void supports_ShouldReturnTrueForUserDtoClass() {
        assertTrue(userValidator.supports(UserDto.class));
    }

    @Test
    void supports_ShouldReturnFalseForOtherClasses() {
        assertFalse(userValidator.supports(String.class));
        assertFalse(userValidator.supports(Object.class));
    }

    @Test
    void validate_ShouldNotThrowExceptionWhenEmailIsUnique() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userValidator.validate(userDto, errors));
        verify(userRepository).findByEmail(userDto.getEmail());
    }

    @Test
    void validate_ShouldThrowAlreadyExistExceptionWhenEmailExists() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(new ru.practicum.shareit.user.model.User()));

        AlreadyExistException exception = assertThrows(
                AlreadyExistException.class,
                () -> userValidator.validate(userDto, errors)
        );

        assertEquals("Такой пользователь уже зарегистрирован", exception.getMessage());
        verify(userRepository).findByEmail(userDto.getEmail());
    }
}