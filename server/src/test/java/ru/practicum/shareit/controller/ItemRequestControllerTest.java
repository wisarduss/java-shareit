package ru.practicum.shareit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private RequestDto requestDto;
    private ItemRequestDto itemRequestDto;
    private User user;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        user = new User(1L, "user@example.com", "User Name", "password");

        requestDto = RequestDto.builder()
                .description("Need a drill")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .created(testTime)
                .items(List.of(
                        ItemDto.builder().id(1L).name("Drill").description("Powerful drill").available(true).build()
                ))
                .build();

        // Настройка SecurityContext
    }

    @Test
    void create_ShouldReturnItemRequestDtoWithItems() {
        when(itemRequestService.create(any(RequestDto.class))).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestController.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertEquals(testTime, result.getCreated());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        verify(itemRequestService).create(requestDto);
    }

    @Test
    void getSelfRequests_ShouldReturnListOfItemRequestDtos() {
        when(itemRequestService.getSelfRequests()).thenReturn(List.of(itemRequestDto));

        List<ItemRequestDto> result = itemRequestController.getSelfRequests();

        assertNotNull(result);
        assertEquals(1, result.size());

        ItemRequestDto dto = result.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(testTime, dto.getCreated());
        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());
        verify(itemRequestService).getSelfRequests();
    }

    @Test
    void getAllRequests_ShouldReturnPaginatedResults() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("created").descending());
        when(itemRequestService.getAll(pageRequest)).thenReturn(List.of(itemRequestDto));

        List<ItemRequestDto> result = itemRequestController.getAllRequests(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRequestService).getAll(pageRequest);
    }


    @Test
    void getRequest_ShouldReturnItemRequestDtoById() {
        when(itemRequestService.get(1L)).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestController.getRequest(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertEquals(testTime, result.getCreated());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        verify(itemRequestService).get(1L);
    }

}