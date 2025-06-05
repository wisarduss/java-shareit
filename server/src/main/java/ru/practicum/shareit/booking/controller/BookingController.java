package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingUpdateDto body) {
        return bookingService.create(body);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved
    ) {
        return bookingService.updateBooking(bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(
            @PathVariable Long bookingId
    ) {
        return bookingService.getBooking(bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(
            @RequestParam(defaultValue = "ALL") RequestBookingStatus state,
            @RequestParam(required = false, defaultValue = "0") final Integer from,
            @RequestParam(required = false, defaultValue = "10") final Integer size
    ) {
        int page = from > 0 ? from / size : from;
        return bookingService.getBookingsByUser(state, PageRequest.of(page, size));
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemBookings(
            @RequestParam(defaultValue = "ALL") RequestBookingStatus state,
            @RequestParam(required = false, defaultValue = "0") final Integer from,
            @RequestParam(required = false, defaultValue = "10") final Integer size
    ) {
        return bookingService.getBookingStatusByOwner(state, PageRequest.of(from, size));
    }
}
