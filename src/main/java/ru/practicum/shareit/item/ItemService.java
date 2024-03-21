package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    ItemDto getByIdItem(Long itemId);

    List<ItemDto> findAllItemsByOwnerId(Long userId);

    List<ItemDto> searchItem(String text);
}
