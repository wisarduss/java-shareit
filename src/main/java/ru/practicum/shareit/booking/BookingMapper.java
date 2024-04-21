package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.MainDto;

@UtilityClass
public class BookingMapper {

    public static BookingDto bookingToBookingDTO(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(MainDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .item(MainDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .build();
    }

    public static Booking bookingDtoToBooking(BookingDto dto, User user, Item item) {
        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(dto.getStatus())
                .item(item)
                .booker(user)
                .build();
    }

    public static Booking bookingDtoToBooking(BookingUpdateDto dto, User user, Item item) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(dto.getStatus())
                .item(item)
                .booker(user)
                .build();
    }
}
