package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = {UserController.class, ErrorHandler.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private static final String URL = "http://localhost:8080/users";

    @Test
    void addEmptyName() throws Exception {
        User user = User.builder()
                .email("max@mail.ru")
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(user)));

        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации данных из запроса.")));
    }

    @Test
    void addNullEmail() throws Exception {
        User user = User.builder()
                .name("Max")
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(user)));

        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации данных из запроса.")));
    }

    @Test
    void addIncorrectEmail() throws Exception {
        User user = User.builder()
                .name("test")
                .email("qwertymail.ru")
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(user)));

        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации данных из запроса.")));
    }


    @Test
    void createDuplicateEmail() throws Exception {
        when(userService.createUser(Mockito.any()))
                .thenThrow(AlreadyExistException.class);

        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("max@mail.ru")
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(status().is5xxServerError());
    }


    @Test
    void updateNotFoundUser() throws Exception {
        when(userService.updateUser(Mockito.anyLong(), Mockito.any()))
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

    @Test
    void createUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("max@mail.ru")
                .build();

        when(userService.createUser(Mockito.any()))
                .thenReturn(userDto);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(status().isOk());
    }


}
