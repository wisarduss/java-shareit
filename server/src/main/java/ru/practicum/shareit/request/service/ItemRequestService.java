package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(RequestDto requestDTO);

    List<ItemRequestDto> getSelfRequests();

    List<ItemRequestDto> getAll(Pageable pageable);

    ItemRequestDto get(Long id);
}
