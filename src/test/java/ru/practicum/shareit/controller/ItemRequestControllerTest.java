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
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = {ItemRequestController.class, ErrorHandler.class})
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String URL = "http://localhost:8080/requests";

    @Test
    void createEmptyDescription() throws Exception {

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(ItemRequestDto.builder().build())));

        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации данных из запроса.")));
    }


    @Test
    void createUserNotFound() throws Exception {
        when(itemRequestService.create(anyLong(), Mockito.any()))
                .thenThrow(IdNotFoundException.class);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .build();
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(itemRequestDto)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void getByIdRequestNotFound() throws Exception {
        when(itemRequestService.get(anyLong(), anyLong()))
                .thenThrow(IdNotFoundException.class);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{requestId}"), 1L)
                .header("X-Sharer-User-Id", 1L));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void create() throws Exception {
        ItemRequestDto itemRequestDTO = ItemRequestDto.builder()
                .description("test")
                .build();
        when(itemRequestService.create(anyLong(), any()))
                .thenReturn(itemRequestDTO);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(itemRequestDTO)));

        response.andExpect(status().isOk());
    }

}
