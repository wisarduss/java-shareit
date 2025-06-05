package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.practicum.shareit.authentication.service.AuthenticationServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User user;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("rawPassword");

        encodedPassword = "encodedPassword";
    }

    @Test
    void register_ShouldEncodePasswordAndSaveUser() {
        // Arrange
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        authenticationService.register(user);

        // Assert
        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getPassword().equals(encodedPassword) &&
                        savedUser.getEmail().equals("test@example.com")
        ));
    }

    @Test
    void register_ShouldHandlePasswordEncodingCorrectly() {
        // Arrange
        String differentPassword = "differentEncodedPassword";
        when(passwordEncoder.encode(user.getPassword())).thenReturn(differentPassword);

        // Act
        authenticationService.register(user);

        // Assert
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getPassword().equals(differentPassword)
        ));
    }

    @Test
    void register_ShouldSaveUserWithAllFields() {
        // Arrange
        User fullUser = new User();
        fullUser.setId(2L);
        fullUser.setEmail("full@example.com");
        fullUser.setName("Full User");
        fullUser.setPassword("fullPassword");

        when(passwordEncoder.encode(fullUser.getPassword())).thenReturn("encodedFullPassword");
        when(userRepository.save(any(User.class))).thenReturn(fullUser);

        // Act
        authenticationService.register(fullUser);

        // Assert
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getId() == 2L &&
                        savedUser.getEmail().equals("full@example.com") &&
                        savedUser.getName().equals("Full User") &&
                        savedUser.getPassword().equals("encodedFullPassword")
        ));
    }
}
