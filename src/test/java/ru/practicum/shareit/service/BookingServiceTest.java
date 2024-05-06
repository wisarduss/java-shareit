package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private EntityManager entityManager;

    @Test
    void bookingValidateEqualsDate() {
        LocalDateTime date = LocalDateTime.now();
        BookingUpdateDto bookingDto = BookingUpdateDto.builder()
                .itemId(1L)
                .start(date)
                .end(date)
                .build();

        assertThrows(ValidateException.class, () -> bookingService.create(1L, bookingDto));
    }

    @Test
    void bookingValidateDate() {
        BookingUpdateDto bookingDTO = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now())
                .build();

        assertThrows(ValidateException.class, () -> bookingService.create(1L, bookingDTO));
    }

    @Test
    void bookingUserNotFound() {
        BookingUpdateDto bookingRequestDTO = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> bookingService.create(1L, bookingRequestDTO));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void bookingItemNotFound() {
        BookingUpdateDto bookingRequestDTO = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> bookingService.create(1L, bookingRequestDTO));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void bookingSelfItemException() {
        BookingUpdateDto bookingRequestDTO = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
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
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(IdNotFoundException.class,
                () -> bookingService.create(1L, bookingRequestDTO));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void bookingItemNotAvailableException() {
        BookingUpdateDto bookingRequestDTO = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
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
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.isItemAvailable(anyLong()))
                .thenReturn(Boolean.FALSE);

        assertThrows(ItemNotAvailableException.class, () -> bookingService.create(2L, bookingRequestDTO));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).isItemAvailable(anyLong());
    }

    @Test
    void booking() {
        BookingUpdateDto bookingRequestDTO = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();


        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.isItemAvailable(anyLong()))
                .thenReturn(Boolean.TRUE);
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDto result = bookingService.create(2L, bookingRequestDTO);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING.name());
        assertThat(result.getStart()).isEqualTo(bookingRequestDTO.getStart());
        assertThat(result.getEnd()).isEqualTo(bookingRequestDTO.getEnd());
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).isItemAvailable(anyLong());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void updateBookingUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> bookingService.updateBooking(1L, 1L, Boolean.TRUE));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateBookingBookingNotFoundException() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> bookingService.updateBooking(1L, 1L, Boolean.TRUE));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateBookingStatusException() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        booking.setStatus(BookingStatus.APPROVED.name());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(ValidateException.class,
                () -> bookingService.updateBooking(1L, 1L, Boolean.TRUE));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateBookingItemException() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> bookingService.updateBooking(1L, 1L, Boolean.TRUE));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateBookingOwnerException() {

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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(IdNotFoundException.class,
                () -> bookingService.updateBooking(2L, 1L, Boolean.TRUE));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateBookingApprove() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        BookingDto result = bookingService.updateBooking(1L, 1L, Boolean.TRUE);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING.name());
        assertThat(result.getStart()).isEqualTo(booking.getStart());
        assertThat(result.getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(2)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).updateItemAvailableById(anyLong(), anyBoolean());
        verify(bookingRepository, times(1)).updateBookingStatusById(anyLong(), any());
    }

    @Test
    void updateBookingDismiss() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        BookingDto result = bookingService.updateBooking(1L, 1L, Boolean.FALSE);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING.name());
        assertThat(result.getStart()).isEqualTo(booking.getStart());
        assertThat(result.getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(2)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).updateBookingStatusById(anyLong(), any());
    }

    @Test
    void getBookingUserException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> bookingService.getBooking(1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingBookingNotFoundException() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> bookingService.getBooking(1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingItemNotFoundException() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> bookingService.getBooking(1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingUserNotFoundException() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(IdNotFoundException.class, () -> bookingService.getBooking(2L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBooking() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        BookingDto result = bookingService.getBooking(1L, 1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.getStart()).isEqualTo(booking.getStart());
        assertThat(result.getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingsByUserException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> bookingService.getBookingsByUser(1L, RequestBookingStatus.ALL, null));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingsByUserAll() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByUser(1L, RequestBookingStatus.ALL, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByUserWaiting() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByUser(1L, RequestBookingStatus.WAITING, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getBookingsByUserRejected() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByUser(1L, RequestBookingStatus.REJECTED, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(),
                any(), any());
    }

    @Test
    void getBookingsByUserCurrent() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentBookingByBookerId(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByUser(1L, RequestBookingStatus.CURRENT, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findCurrentBookingByBookerId(anyLong(), any());
    }

    @Test
    void getBookingsByUserPast() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findPastBookingByBookerId(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByUser(1L, RequestBookingStatus.PAST, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findPastBookingByBookerId(anyLong(), any());
    }

    @Test
    void getBookingsByUserFuture() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findFutureBookingByBookerId(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByUser(1L, RequestBookingStatus.FUTURE, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findFutureBookingByBookerId(anyLong(), any());
    }

    @Test
    void getBookingStatusByOwnerException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.ALL, null));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingStatusByOwnerItemException() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(Collections.emptyList());

        assertThrows(IdNotFoundException.class,
                () -> bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.ALL, null));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
    }


    @Test
    void getBookingStatusByOwnerAll() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.ALL, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
        verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdOrderByStartDesc(anyLong(),
                any());
    }

    @Test
    void getBookingStatusByOwnerWaiting() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(anyLong(), any(),
                any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.WAITING, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
        verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(), any());
    }

    @Test
    void getBookingStatusByOwnerRejected() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(anyLong(), any(),
                any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.REJECTED, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
        verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(), any());
    }

    @Test
    void getBookingStatusByOwnerCurrent() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.findCurrentBookingByOwnerId(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.CURRENT, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
        verify(bookingRepository, times(1)).findCurrentBookingByOwnerId(anyLong(), any());
    }

    @Test
    void getBookingStatusByOwnerPast() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(),
                any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.PAST, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
        verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(), any());
    }

    @Test
    void getBookingStatusByOwnerFuture() {
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
                .id(2L)
                .start(LocalDateTime.parse("2030-01-01T00:00:00"))
                .end(LocalDateTime.parse("2030-02-01T00:00:00"))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING.name())
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(),
                any()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.FUTURE, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getItem()).isNotNull();
        assertThat(result.get(0).getBooker()).isNotNull();

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
        verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(), any());
    }

}
