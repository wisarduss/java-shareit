package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    public void createUserNotFound() {
        RequestDto request = RequestDto.builder()
                .description("description")
                .build();

        when(userRepository.findById(anyLong()))
                .thenThrow(IdNotFoundException.class);

        assertThrows(IdNotFoundException.class, () -> itemRequestService.create(1L, request));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void create() {
        RequestDto request = RequestDto.builder()
                .description("description")
                .build();

        User user = User.builder()
                .id(1L)
                .name("max")
                .email("max@mail.ru")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(user)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.create(1L, request);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemRequest.getId());
        assertThat(result.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(result.getCreated()).isEqualTo(itemRequest.getCreated());

        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    public void getByIdItemNotFound() {
        User user = User.builder()
                .id(1L)
                .name("max")
                .email("max@mail.ru")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> itemRequestService.get(1L, 1L));

        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findById(any());
    }

    @Test
    public void getById() {
        User user = User.builder()
                .id(1L)
                .name("max")
                .email("max@mail.ru")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(user)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest_Id(anyLong()))
                .thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.get(1L, 1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemRequest.getId());
        assertThat(result.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(result.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(result.getItems()).isEmpty();

        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findById(any());
    }

    @Test
    public void getAll() {
        User user = User.builder()
                .id(1L)
                .name("max")
                .email("max@mail.ru")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(user)
                .build();

        when(itemRequestRepository.findAllWithoutRequesterId(anyLong(), any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = itemRequestService.getAll(1L, PageRequest.ofSize(1));
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(itemRequest.getId());
        assertThat(result.get(0).getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(result.get(0).getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(result.get(0).getItems()).isEmpty();

        verify(itemRequestRepository, times(1)).findAllWithoutRequesterId(anyLong(), any());
    }


    @Test
    public void getAllSelfRequests() {
        RequestDto request = RequestDto.builder()
                .description("description")
                .build();

        User user = User.builder()
                .id(1L)
                .name("max")
                .email("max@mail.ru")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(user)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(anyLong()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = itemRequestService.getSelfRequests(1L);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(itemRequest.getId());
        assertThat(result.get(0).getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(result.get(0).getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(result.get(0).getItems()).isEmpty();

        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findAllByRequesterId(anyLong());
    }


}
