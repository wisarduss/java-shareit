package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto, Long itemId);

    ItemFullDto getByIdItem(Long itemId);

    List<ItemFullDto> findAllItemsByOwnerId(Pageable pageable);

    List<ItemDto> searchItem(String text, Pageable pageable);

    CommentDto makeComment(Long itemId, CommentUpdateDto text);
}
