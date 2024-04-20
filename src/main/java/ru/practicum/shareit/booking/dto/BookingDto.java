package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.utils.MainDto;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private MainDto booker;
    private MainDto item;
}
