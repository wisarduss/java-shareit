package ru.practicum.shareit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.authentication.controller.AuthController;
import ru.practicum.shareit.authentication.dto.AuthenticationDto;
import ru.practicum.shareit.authentication.service.AuthenticationService;
import ru.practicum.shareit.exception.BadRegistrationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.utils.JWTUtil;
import ru.practicum.shareit.utils.UserValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserValidator userValidator;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AuthController authController;

    private UserDto userDto;
    private AuthenticationDto authDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .email("user@example.com")
                .name("Test User")
                .password("password123")
                .build();


        authDto = AuthenticationDto.builder()
                .email("user@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .email("user@example.com")
                .name("Test User")
                .password("password123")
                .build();
    }


    @Test
    void performRegistration_ShouldThrowException_WhenValidationFails() {
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BadRegistrationException.class,
                () -> authController.performRegistration(userDto, bindingResult));

        verify(authenticationService, never()).register(any());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void performLogin_ShouldReturnToken_WhenValidCredentials() {
        when(jwtUtil.generateToken(anyString())).thenReturn("testToken");

        String result = authController.performLogin(authDto);

        assertEquals("testToken", result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(authDto.getEmail());
    }

}
