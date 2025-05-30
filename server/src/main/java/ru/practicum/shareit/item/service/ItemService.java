package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.category.dto.CategoryDto;
import ru.practicum.shareit.item.dto.*;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto, Long itemId);

    ItemFullDto getByIdItem(Long itemId);

    List<ItemFullDto> findAllItemsByOwnerId(Pageable pageable);

    List<ItemDto> searchItem(String text, Pageable pageable);

    CommentDto makeComment(Long itemId, CommentUpdateDto text);

    List<ItemResponseDto> getItemForCatId(Long catId);

    Map<String, List<ItemResponseDto>> getAll();
}
