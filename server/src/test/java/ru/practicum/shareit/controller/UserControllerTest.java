package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.authentication.security.PersonDetails;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = {UserController.class, ErrorHandler.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    private User authenticatedUser;

    private static final String URL = "http://localhost:8080/users";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setEmail("test@example.com");

        PersonDetails personDetails = new PersonDetails(authenticatedUser);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(personDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @WithMockUser
    void addEmptyName() throws Exception {
        User user = User.builder()
                .name("max")
                .email("max@mail.ru")
                .build();

        authenticatedUser = user;

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void addNullEmail() throws Exception {
        User user = User.builder()
                .name("Max")
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .content(objectMapper.writeValueAsString(user)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void addIncorrectEmail() throws Exception {
        User user = User.builder()
                .name("test")
                .email("qwertymail.ru")
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(user)));

        response.andExpect(status().is4xxClientError());
    }


    @Test
    @WithMockUser
    void createDuplicateEmail() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("max@mail.ru")
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(status().is4xxClientError());
    }


    @Test
    @WithMockUser
    void updateNotFoundUser() throws Exception {
        when(userService.updateUser( Mockito.any()))
                .thenThrow(IdNotFoundException.class);

        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("max@mail.ru")
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch(URL.concat("/{id}"), 1L)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(status().is4xxClientError());
    }
}
