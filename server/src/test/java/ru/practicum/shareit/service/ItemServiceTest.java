package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @Test
    void addUserException() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> itemService.createItem(1L, itemDto));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void addWithRequestId() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        itemDto.setRequestId(1L);
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(user)
                .available(Boolean.TRUE)
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("test")
                .requester(user)
                .build();

        item.setRequest(itemRequest);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.createItem(1L, itemDto);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(result.getRequestId()).isEqualTo(item.getRequest().getId());

        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void addWithoutRequestId() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(user)
                .available(Boolean.TRUE)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.createItem(1L, itemDto);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item.getAvailable());

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void editUserException() {

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(user)
                .available(Boolean.TRUE)
                .build();

        when(itemRepository.getById(anyLong()))
                .thenReturn(item);

        assertThrows(IdNotFoundException.class, () -> itemService.updateItem(2L, itemDto, 1L));
        verify(itemRepository, times(1)).getById(any());
    }

    @Test
    void edit() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("description")
                .available(Boolean.TRUE)
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(user)
                .available(Boolean.TRUE)
                .build();

        when(itemRepository.getById(anyLong()))
                .thenReturn(item);
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.updateItem(1L, itemDto, 1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemDto.getId());
        assertThat(result.getName()).isEqualTo(itemDto.getName());
        assertThat(result.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(result.getAvailable()).isEqualTo(itemDto.getAvailable());

        verify(itemRepository, times(1)).getById(any());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void getByIdNotFoundException() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> itemService.getByIdItem(1L, 1L));
        verify(itemRepository, times(1)).findById(anyLong());

    }

    @Test
    void getById() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("test")
                .available(Boolean.TRUE)
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(user)
                .available(Boolean.TRUE)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED.name())
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("test")
                .item(item)
                .user(user)
                .build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsByItemId(anyLong()))
                .thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));

        ItemFullDto result = itemService.getByIdItem(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemDto.getId());
        assertThat(result.getName()).isEqualTo(itemDto.getName());
        assertThat(result.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(result.getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getId()).isEqualTo(comment.getId());
        assertThat(result.getComments().get(0).getText()).isEqualTo(comment.getText());
        assertThat(result.getComments().get(0).getAuthorName()).isEqualTo(comment.getUser().getName());

        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingsByItemId(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());

    }

    @Test
    void getByIdAllBookingDate() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("test")
                .available(Boolean.TRUE)
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(user)
                .available(Boolean.TRUE)
                .build();

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED.name())
                .build();

        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking1.setEnd(LocalDateTime.now().minusDays(1));
        Booking booking2 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED.name())
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("test")
                .item(item)
                .user(user)
                .build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsByItemId(anyLong()))
                .thenReturn(List.of(booking1, booking2));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));

        ItemFullDto result = itemService.getByIdItem(2L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemDto.getId());
        assertThat(result.getName()).isEqualTo(itemDto.getName());
        assertThat(result.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(result.getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getId()).isEqualTo(comment.getId());
        assertThat(result.getComments().get(0).getText()).isEqualTo(comment.getText());
        assertThat(result.getComments().get(0).getAuthorName()).isEqualTo(comment.getUser().getName());

        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingsByItemId(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());

    }

    @Test
    void getUserItems() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("test")
                .available(Boolean.TRUE)
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(user)
                .available(Boolean.TRUE)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED.name())
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("test")
                .item(item)
                .user(user)
                .build();

        when(itemRepository.findItemsByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsByItemId(anyLong()))
                .thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));

        List<ItemFullDto> result = itemService.findAllItemsByOwnerId(2L, null);
        ItemFullDto resultItemDTO = result.get(0);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(resultItemDTO.getId()).isEqualTo(itemDto.getId());
        assertThat(resultItemDTO.getName()).isEqualTo(itemDto.getName());
        assertThat(resultItemDTO.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(resultItemDTO.getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(resultItemDTO.getNextBooking()).isNull();
        assertThat(resultItemDTO.getLastBooking()).isNotNull();
        assertThat(resultItemDTO.getComments()).hasSize(1);
        assertThat(resultItemDTO.getComments().get(0).getId()).isEqualTo(comment.getId());
        assertThat(resultItemDTO.getComments().get(0).getText()).isEqualTo(comment.getText());
        assertThat(resultItemDTO.getComments().get(0).getAuthorName()).isEqualTo(
                comment.getUser().getName());

        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemsByOwnerId(anyLong(), any());
        verify(bookingRepository, times(1)).findBookingsByItemId(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());

    }

    @Test
    void searchEmptyText() {
        List<ItemDto> result = itemService.searchItem("", null);
        assertThat(result).isEmpty();
    }

    @Test
    void search() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(user)
                .available(Boolean.TRUE)
                .build();

        when(itemRepository.search(any(), any()))
                .thenReturn(List.of(item));

        List<ItemDto> result = itemService.searchItem("test", null);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(item.getName());
        assertThat(result.get(0).getDescription()).isEqualTo(item.getDescription());
        assertThat(result.get(0).getAvailable()).isEqualTo(item.getAvailable());

        verify(itemRepository, times(1)).search(any(), any());
    }

    @Test
    void commentBookingException() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(user)
                .available(Boolean.TRUE)
                .build();

        when(bookingRepository.existsBookingByBookerIdAndStatus(anyLong(), any()))
                .thenReturn(Boolean.FALSE);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        assertThrows(ValidateException.class,
                () -> itemService.makeComment(1L, 1L, CommentUpdateDto.builder()
                        .text("test")
                        .build()));
    }

    @Test
    void comment() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(user)
                .available(Boolean.TRUE)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("test")
                .item(item)
                .user(user)
                .build();

        when(bookingRepository.existsBookingByBookerIdAndStatus(anyLong(), any()))
                .thenReturn(Boolean.TRUE);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto result = itemService.makeComment(1L, 1L, CommentUpdateDto.builder()
                .text("test")
                .build());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("test");
        assertThat(result.getAuthorName()).isEqualTo("name");

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any());
        verify(bookingRepository, times(1)).existsBookingByBookerIdAndStatus(anyLong(), any());
    }

}
