package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequestDto itemRequestToItemRequestDTO(ItemRequest entity) {
        return ItemRequestDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .created(entity.getCreated())
                .items(getItems(entity))
                .build();
    }

    public static ItemRequest itemRequestDtoToItemRequest(User user, RequestDto requestDTO) {
        return ItemRequest.builder()
                .description(requestDTO.getDescription())
                .requester(user)
                .build();
    }

    private static List<ItemDto> getItems(ItemRequest entity) {
        if (entity.getItems() == null) {
            return Collections.emptyList();
        }
        if (entity.getItems().isEmpty()) {
            return Collections.emptyList();
        }
        return entity.getItems().stream()
                .map(ItemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }
}
