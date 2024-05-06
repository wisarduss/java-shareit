package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, RequestDto requestDTO);

    List<ItemRequestDto> getSelfRequests(Long userId);

    List<ItemRequestDto> getAll(Long userId, Pageable pageable);

    ItemRequestDto get(Long userId, Long id);
}
