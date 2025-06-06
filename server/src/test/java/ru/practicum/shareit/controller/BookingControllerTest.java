package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.IdNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = {BookingController.class, ErrorHandler.class})
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private static final String URL = "http://localhost:8080/bookings";

    @Test
    void bookEmptyItemId() throws Exception {

        BookingUpdateDto booking = BookingUpdateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(booking)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void bookEmptyStart() throws Exception {

        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .end(LocalDateTime.now().plusDays(1))
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(booking)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void bookEmptyEnd() throws Exception {

        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(booking)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void bookPastStart() throws Exception {

        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(booking)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void bookPastEnd() throws Exception {

        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(booking)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void bookUserNotFound() throws Exception {
        when(bookingService.create(any()))
                .thenThrow(IdNotFoundException.class);

        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(booking)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void updateBookingNotFound() throws Exception {
        when(
                bookingService.updateBooking(anyLong(), Mockito.anyBoolean()))
                .thenThrow(IdNotFoundException.class);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch(URL.concat("/{bookingId}"), 1L)
                .param("approved", Boolean.FALSE.toString())
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void getBookingItemNotFound() throws Exception {
        when(bookingService.getBooking(anyLong()))
                .thenThrow(IdNotFoundException.class);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{bookingId}"), 1L)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void bookItemNotAvailable() throws Exception {
        when(bookingService.create(any()))
                .thenThrow(IdNotFoundException.class);

        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(booking)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void booking() throws Exception {
        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto expected = BookingDto.builder()
                .id(1L)
                .status("WAITING")
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(bookingService.create(any()))
                .thenReturn(expected);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .header("Content-Type", "application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(booking)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void changeBooking() throws Exception {
        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        BookingDto expected = BookingDto.builder()
                .id(1L)
                .status("WAITING")
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(bookingService.updateBooking(anyLong(), any()))
                .thenReturn(expected);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch(URL.concat("/{bookingId}"), 1L)
                .param("approved", Boolean.FALSE.toString())
                .header("Content-Type", "application/json")
                .content(mapper.writeValueAsString(booking)));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void getById() throws Exception {
        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        BookingDto expected = BookingDto.builder()
                .id(1L)
                .status("WAITING")
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(bookingService.getBooking(anyLong()))
                .thenReturn(expected);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{bookingId}"), 1L));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    void getUserBookings() throws Exception {
        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        BookingDto expected = BookingDto.builder()
                .id(1L)
                .status("WAITING")
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(bookingService.getBookingsByUser(any(), any()))
                .thenReturn(List.of(expected));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URL));

        response.andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void getUserItemBookings() throws Exception {
        BookingUpdateDto booking = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        BookingDto expected = BookingDto.builder()
                .id(1L)
                .status("WAITING")
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(bookingService.getBookingStatusByOwner(any(), any()))
                .thenReturn(List.of(expected));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/owner")));


        response.andExpect(status().isOk());
    }
}
