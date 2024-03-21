package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto, Long itemId);

    ItemDto getById(Long itemId);

    List<ItemDto> findAllItemsByOwnerId(Long userId);

    List<ItemDto> search(String text);
}
