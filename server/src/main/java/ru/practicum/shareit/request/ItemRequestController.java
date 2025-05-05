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
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(
            @Valid @RequestBody final RequestDto requestDTO) {
        return itemRequestService.create(requestDTO);
    }

    @GetMapping
    public List<ItemRequestDto> getSelfRequests()    {
        return itemRequestService.getSelfRequests();
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestParam(required = false, defaultValue = "0") final Integer from,
            @RequestParam(required = false, defaultValue = "10") final Integer size
    ) {
        return itemRequestService.getAll(PageRequest.of(from, size, Sort.by("created").descending()));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(
            @PathVariable("requestId") final Long requestId) {
        return itemRequestService.get(requestId);
    }
}
