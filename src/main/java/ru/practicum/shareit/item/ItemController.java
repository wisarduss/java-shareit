package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader(USER_ID) Long userId,
                             @RequestBody ItemDto itemDto,
                             @PathVariable Long itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemFullDto getByIdItem(@RequestHeader(USER_ID) Long userId, @PathVariable Long itemId) {
        return itemService.getByIdItem(userId, itemId);
    }

    @GetMapping
    public List<ItemFullDto> findAllItemsByOwnerId(@RequestHeader(USER_ID) Long userId,
                                                   @RequestParam(required = false, defaultValue = "0") final Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") final Integer size) {
        return itemService.findAllItemsByOwnerId(userId, PageRequest.of(from, size));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(required = false, defaultValue = "0") final Integer from,
                                    @RequestParam(required = false, defaultValue = "10") final Integer size) {
        return itemService.searchItem(text, PageRequest.of(from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(
            @RequestHeader(USER_ID) long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentUpdateDto text) {
        return itemService.makeComment(userId, itemId, text);
    }
}
