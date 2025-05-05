package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingUpdateDto bookingParam);

    BookingDto updateBooking(Long bookingId, Boolean isApproved);

    BookingDto getBooking(Long bookingId);

    List<BookingDto> getBookingsByUser(RequestBookingStatus state, Pageable pageable);

    List<BookingDto> getBookingStatusByOwner(RequestBookingStatus state, Pageable pageable);
}
