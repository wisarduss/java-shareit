package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.authentication.config.JWTFilter;
import ru.practicum.shareit.authentication.controller.AuthController;
import ru.practicum.shareit.authentication.service.AuthenticationService;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.JWTUtil;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JWTFilter jwtFilter;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private AuthController authController;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;


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

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.create(request);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemRequest.getId());
        assertThat(result.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(result.getCreated()).isEqualTo(itemRequest.getCreated());

        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    public void getByIdItemNotFound() {
        User user = User.builder()
                .id(1L)
                .name("max")
                .email("max@mail.ru")
                .build();

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> itemRequestService.get(1L));

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

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = itemRequestService.get(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemRequest.getId());
        assertThat(result.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(result.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(result.getItems()).isEmpty();

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
        when(userService.getAuthenticatedUser()).thenReturn(user);

        when(itemRequestRepository.findAllWithoutRequesterId(anyLong(), any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = itemRequestService.getAll(PageRequest.ofSize(1));
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

        when(userService.getAuthenticatedUser()).thenReturn(user);

        when(itemRequestRepository.findAllByRequesterId(anyLong()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = itemRequestService.getSelfRequests();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(itemRequest.getId());
        assertThat(result.get(0).getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(result.get(0).getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(result.get(0).getItems()).isEmpty();

        verify(itemRequestRepository, times(1)).findAllByRequesterId(anyLong());
    }


}
