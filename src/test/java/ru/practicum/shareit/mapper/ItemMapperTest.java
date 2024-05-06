package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

    @Test
    void toDTOWithoutRequestTest() {
        Item item = Item.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        ItemDto result = ItemMapper.itemToItemDto(item);
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void toDTOWithRequestTest() {
        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .available(Boolean.TRUE)
                .request(ItemRequest.builder()
                        .id(1L)
                        .build())
                .build();

        ItemDto result = ItemMapper.itemToItemDto(item);
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(result.getRequestId()).isEqualTo(item.getRequest().getId());
    }

    @Test
    void toFullDTOEmptyFieldsTest() {
        Item item = Item.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();
        List<CommentDto> comments = Collections.emptyList();

        ItemFullDto result = ItemMapper.itemToItemFullDto(item, comments, null, null);
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isEmpty();
    }

    @Test
    void toFullDTOAllFieldsTest() {
        Item item = Item.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();
        ;
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("authorName")
                .created(LocalDateTime.now())
                .build();
        List<CommentDto> comments = List.of(comment);
        User user = User.builder()
                .id(1L)
                .name("max")
                .email("max@mail.ru")
                .build();
        Booking lastBooking = Booking.builder()
                .id(1L)
                .booker(user)
                .build();
        Booking nextBooking = Booking.builder()
                .id(2L)
                .booker(user)
                .build();

        ItemFullDto result = ItemMapper.itemToItemFullDto(item, comments, lastBooking, nextBooking);
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(result.getLastBooking().getId()).isEqualTo(lastBooking.getId());
        assertThat(result.getLastBooking().getBookerId()).isEqualTo(lastBooking.getBooker().getId());
        assertThat(result.getNextBooking().getId()).isEqualTo(nextBooking.getId());
        assertThat(result.getNextBooking().getBookerId()).isEqualTo(nextBooking.getBooker().getId());
        assertThat(result.getComments()).usingRecursiveComparison().isEqualTo(comments);
    }

}
