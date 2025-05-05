package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDto create(RequestDto requestDTO) {
        log.debug("Получен запрос на создание itemRequest");
        User user = userService.getAuthenticatedUser();

        var result = itemRequestRepository.save(ItemRequestMapper.itemRequestDtoToItemRequest(user, requestDTO));
        return ItemRequestMapper.itemRequestToItemRequestDTO(result);
    }

    @Override
    public List<ItemRequestDto> getSelfRequests() {
        log.debug("Получен запрос получение самого же itemRequest");
        User user = userService.getAuthenticatedUser();

        var result = itemRequestRepository.findAllByRequesterId(user.getId());
        return result.stream()
                .map(ItemRequestMapper::itemRequestToItemRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Pageable pageable) {
        log.debug("Получен запрос получение всех itemRequest");
        User user = userService.getAuthenticatedUser();
        List<ItemRequest> itemRequests = itemRequestRepository.findAllWithoutRequesterId(user.getId(),
                pageable);
        return itemRequests.stream()
                .map(ItemRequestMapper::itemRequestToItemRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto get(Long id) {
        log.debug("Получен запрос получение itemRequest пользователя");

        ItemRequest request = itemRequestRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Запрашиваемая вещь не с id = " + id + " не найдена"));
        return ItemRequestMapper.itemRequestToItemRequestDTO(request);
    }

}
