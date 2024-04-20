package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;

import java.util.List;

public interface BookingService {

    BookingDto create(Long bookerId, BookingUpdateDto bookingParam);

    BookingDto updateBooking(Long ownerId, Long bookingId, Boolean isApproved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getBookingsByUser(Long bookerId, RequestBookingStatus state);

    List<BookingDto> getBookingStatusByOwner(Long ownerId, RequestBookingStatus state);
}
