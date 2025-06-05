package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.practicum.shareit.authentication.security.PersonDetails;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.userDto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private PersonDetails personDetails;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@example.com")
                .name("User Name")
                .password("password123")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .email("user@example.com")
                .name("User Name")
                .password("password123")
                .build();

        personDetails = new PersonDetails(user);
    }

    @Test
    void getAuthenticatedUser_ShouldReturnUser_WhenAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(personDetails);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User result = userService.getAuthenticatedUser();

        assertEquals(user, result);
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void getAuthenticatedUser_ShouldThrowNotOwnerException_WhenUserNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(personDetails);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(NotOwnerException.class, () -> userService.getAuthenticatedUser());
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        var result = userService.loadUserByUsername(user.getEmail());

        assertNotNull(result);
        assertEquals(personDetails.getUsername(), result.getUsername());
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("unknown@example.com"));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto.getId(), result.get(0).getId());
        assertEquals(userDto.getEmail(), result.get(0).getEmail());
        assertEquals(userDto.getName(), result.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void updateUser_ShouldUpdateNameAndPassword_WhenProvided() {
        UserDto updateDto = UserDto.builder()
                .name("New Name")
                .password("newPassword123")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(personDetails);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(updateDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("newPassword123", result.getPassword());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldUpdateEmail_WhenNewEmailProvided() {
        UserDto updateDto = UserDto.builder()
                .email("new@example.com")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(personDetails);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.updateUser(updateDto);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        assertEquals(user.getName(), result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldNotUpdate_WhenEmailSameAsCurrent() {
        UserDto updateDto = UserDto.builder()
                .email(user.getEmail())
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(personDetails);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.updateUser(updateDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getName(), result.getName());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void getUserById_ShouldThrowIdNotFoundException_WhenUserNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void removeUserById_ShouldCallRepositoryDelete() {
        Long userId = 1L;

        userService.removeUserById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void removeYourSelfProfile_ShouldDeleteAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(personDetails);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        userService.removeYourSelfProfile();

        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void updateUser_ShouldHandlePasswordUpdateCorrectly() {
        UserDto updateDto = UserDto.builder()
                .password("newSecurePassword123")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(personDetails);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.updateUser(updateDto);

        assertNotNull(result);
        assertEquals("newSecurePassword123", result.getPassword());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getName(), result.getName());
        verify(userRepository).save(any(User.class));
    }
}
