package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.category.model.Category;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;

@UtilityClass
public class ItemMapper {

    public static ItemDto itemToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .photoUrl(item.getPhotoUrl())
                .price(item.getPrice())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static ItemResponseDto itemToItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .photoUrl(item.getPhotoUrl())
                .price(item.getPrice())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static ItemFullDto itemToItemFullDto(Item item, List<CommentDto> comments, Booking lastBooking,
                                                Booking nextBooking) {
        return ItemFullDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .photoUrl(item.getPhotoUrl())
                .price(item.getPrice())
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
                .price(dto.getPrice())
                .photoUrl(dto.getPhotoUrl())
                .available(dto.getAvailable())
                .owner(owner)
                .build();
    }

    public static Item itemDtoToItemWithRequest(ItemDto dto, Set<Category> categories,
                                                User owner, ItemRequest request) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .photoUrl(dto.getPhotoUrl())
                .price(dto.getPrice())
                .available(dto.getAvailable())
                .owner(owner)
                .request(request)
                .categories(categories)
                .build();
    }

    public static Item itemDtoToItemWithoutRequest(ItemDto dto, Set<Category> categories,
                                                User owner) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .photoUrl(dto.getPhotoUrl())
                .price(dto.getPrice())
                .available(dto.getAvailable())
                .owner(owner)
                .categories(categories)
                .build();
    }
}
