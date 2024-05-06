package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(Long userId, RequestDto requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        var result = itemRequestRepository.save(ItemRequestMapper.itemRequestDtoToItemRequest(user, requestDTO));
        return ItemRequestMapper.itemRequestToItemRequestDTO(result);
    }

    @Override
    public List<ItemRequestDto> getSelfRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));
        var result = itemRequestRepository.findAllByRequesterId(userId);
        return result.stream()
                .map(ItemRequestMapper::itemRequestToItemRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Pageable pageable) {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllWithoutRequesterId(userId,
                pageable);
        return itemRequests.stream()
                .map(ItemRequestMapper::itemRequestToItemRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto get(Long userId, Long id) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));
        ItemRequest request = itemRequestRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Запрашиваемая вещь не с id = " + id + " не найдена"));
        return ItemRequestMapper.itemRequestToItemRequestDTO(request);
    }

}
