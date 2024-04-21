package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
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
    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                             @Valid @RequestBody BookingUpdateDto body) {
        return bookingService.create(userId, body);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(
            @RequestHeader(REQUEST_HEADER_USER_ID) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved
    ) {
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(
            @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
            @PathVariable Long bookingId
    ) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(
            @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") RequestBookingStatus state
    ) {
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemBookings(
            @RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") RequestBookingStatus state
    ) {
        return bookingService.getBookingStatusByOwner(userId, state);
    }
}
