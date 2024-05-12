package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = {ItemController.class, ErrorHandler.class})
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    private static final String URL = "http://localhost:8080/items";

    @Test
    void addEmptyName() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .description("description")
                .available(Boolean.FALSE)
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(itemDto)));

        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации данных из запроса.")));

    }

    @Test
    void addEmptyDescription() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("keyboard")
                .available(Boolean.TRUE)
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(item)));

        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации данных из запроса.")));
    }

    @Test
    void addNullAvailable() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(item)));

        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации данных из запроса.")));
    }

    @Test
    void addUserNotFound() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        when(itemService.createItem(anyLong(), any()))
                .thenThrow(IdNotFoundException.class);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(itemDto)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void getByIdNotFound() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        when(itemService.getByIdItem(anyLong(), anyLong()))
                .thenThrow(IdNotFoundException.class);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{itemId}"), 1L)
                .header("X-Sharer-User-Id", 1L));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void addItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        when(itemService.createItem(anyLong(), any()))
                .thenReturn(itemDto);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("X-Sharer-User-Id", 1L)
                .header("Content-Type", "application/json")
                .content(mapper.writeValueAsString(itemDto)));

        response.andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        ItemFullDto itemFullDto = ItemFullDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .comments(Collections.emptyList())
                .lastBooking(null)
                .nextBooking(null)
                .build();
        when(itemService.getByIdItem(anyLong(), anyLong()))
                .thenReturn(itemFullDto);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{itemId}"), 1L)
                .header("X-Sharer-User-Id", 1L));

        response.andExpect(status().isOk());
    }

    @Test
    void getUsersItems() throws Exception {
        ItemFullDto itemFullDto = ItemFullDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .comments(Collections.emptyList())
                .lastBooking(null)
                .nextBooking(null)
                .build();
        when(itemService.findAllItemsByOwnerId(anyLong(), any()))
                .thenReturn(List.of(itemFullDto));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URL)
                .header("X-Sharer-User-Id", 1L));

        response.andExpect(status().isOk());
    }

    @Test
    void search() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        when(itemService.searchItem(any(), any()))
                .thenReturn(List.of(itemDto));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/search"))
                .param("text", "test")
                .header("X-Sharer-User-Id", 1L));

        response.andExpect(status().isOk());
    }


}
