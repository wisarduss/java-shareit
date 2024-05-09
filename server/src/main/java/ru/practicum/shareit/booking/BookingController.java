package ru.practicum.shareit.booking;

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
    public static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID) Long userId,
                             @Valid @RequestBody BookingUpdateDto body) {
        return bookingService.create(userId, body);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(
            @RequestHeader(USER_ID) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved
    ) {
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long bookingId
    ) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") RequestBookingStatus state,
            @RequestParam(required = false, defaultValue = "0") final Integer from,
            @RequestParam(required = false, defaultValue = "10") final Integer size
    ) {
        int page = from > 0 ? from / size : from;
        return bookingService.getBookingsByUser(userId, state, PageRequest.of(page, size));
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemBookings(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") RequestBookingStatus state,
            @RequestParam(required = false, defaultValue = "0") final Integer from,
            @RequestParam(required = false, defaultValue = "10") final Integer size
    ) {
        return bookingService.getBookingStatusByOwner(userId, state, PageRequest.of(from, size));
    }
}
