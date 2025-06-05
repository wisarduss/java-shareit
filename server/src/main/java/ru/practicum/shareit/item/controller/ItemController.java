package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto,
                             @PathVariable Long itemId) {
        return itemService.updateItem(itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemFullDto getByIdItem(@PathVariable Long itemId) {
        return itemService.getByIdItem(itemId);
    }

    @GetMapping
    public List<ItemFullDto> findAllItemsByOwnerId(@RequestParam(required = false, defaultValue = "0") final Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") final Integer size) {
        return itemService.findAllItemsByOwnerId(PageRequest.of(from, size));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(required = false, defaultValue = "0") final Integer from,
                                    @RequestParam(required = false, defaultValue = "10") final Integer size) {
        return itemService.searchItem(text, PageRequest.of(from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(
            @PathVariable Long itemId,
            @RequestBody @Valid CommentUpdateDto text) {
        return itemService.makeComment(itemId, text);
    }
}
