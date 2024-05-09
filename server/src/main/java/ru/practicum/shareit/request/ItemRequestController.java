package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    public static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(USER_ID) Long userId,
            @Valid @RequestBody final RequestDto requestDTO) {
        return itemRequestService.create(userId, requestDTO);
    }

    @GetMapping
    public List<ItemRequestDto> getSelfRequests(@RequestHeader(USER_ID) Long userId) {
        return itemRequestService.getSelfRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "0") final Integer from,
            @RequestParam(required = false, defaultValue = "10") final Integer size
    ) {
        return itemRequestService.getAll(userId, PageRequest.of(from, size, Sort.by("created").descending()));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable("requestId") final Long requestId) {
        return itemRequestService.get(userId, requestId);
    }
}
