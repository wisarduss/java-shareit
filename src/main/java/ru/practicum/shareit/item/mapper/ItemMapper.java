package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto itemToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemFullDto itemToItemFullDTO(Item item, List<CommentDto> comments, Booking lastBooking,
                                                Booking nextBooking) {
        return ItemFullDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking != null ? ItemBookingDto.builder()
                        .id(lastBooking.getId())
                        .bookerId(lastBooking.getBooker().getId())
                        .build() : null)
                .nextBooking(nextBooking != null ? ItemBookingDto.builder()
                        .id(nextBooking.getId())
                        .bookerId(nextBooking.getBooker().getId())
                        .build() : null)
                .comments(comments)
                .build();
    }

    public static Item itemDtoToItem(ItemDto dto, User owner) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .build();
    }
}
