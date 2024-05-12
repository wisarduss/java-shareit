package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    ItemFullDto getByIdItem(Long userId, Long itemId);

    List<ItemFullDto> findAllItemsByOwnerId(Long userId, Pageable pageable);

    List<ItemDto> searchItem(String text, Pageable pageable);

    CommentDto makeComment(Long userId, Long itemId, CommentUpdateDto text);
}
