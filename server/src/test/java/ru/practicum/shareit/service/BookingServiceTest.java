package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserService userService;

    private final User testUser = User.builder()
            .id(1L)
            .name("name")
            .email("test@test.ru")
            .build();

    private final Item testItem = Item.builder()
            .id(1L)
            .name("test")
            .description("test")
            .owner(testUser)
            .available(true)
            .build();

    private final Booking testBooking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .item(testItem)
            .booker(testUser)
            .status(BookingStatus.WAITING.name())
            .build();

    @Test
    void create_shouldThrowExceptionWhenDatesEqual() {
        BookingUpdateDto dto = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();


        assertThrows(ValidateException.class, () -> bookingService.create(dto));
    }

    @Test
    void create_shouldThrowExceptionWhenStartAfterEnd() {
        BookingUpdateDto dto = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now())
                .build();

        assertThrows(ValidateException.class, () -> bookingService.create(dto));
    }

    @Test
    void create_shouldThrowExceptionWhenUserNotFound() {
        BookingUpdateDto dto = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(testUser);

        assertThrows(IdNotFoundException.class, () -> bookingService.create(dto));
    }

    @Test
    void create_shouldThrowExceptionWhenItemNotFound() {
        BookingUpdateDto dto = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () -> bookingService.create(dto));
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void create_shouldThrowExceptionWhenBookingOwnItem() {
        BookingUpdateDto dto = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(userService.getAuthenticatedUser()).thenReturn(testUser);

        assertThrows(IdNotFoundException.class, () -> bookingService.create(dto));
    }

    @Test
    @WithMockUser
    void create_shouldThrowExceptionWhenItemNotAvailable() {
        User anotherUser = User.builder().id(2L).build();
        BookingUpdateDto dto = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Item testItemthis = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(anotherUser)
                .available(true)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItemthis));
        when(itemRepository.isItemAvailable(anyLong())).thenReturn(false);

        when(userService.getAuthenticatedUser()).thenReturn(testUser);

        assertThrows(ItemNotAvailableException.class, () -> bookingService.create(dto));
    }

    @Test
    void create_shouldCreateBookingSuccessfully() {
        User anotherUser = User.builder().id(2L).build();
        BookingUpdateDto dto = BookingUpdateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Item testItem = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .owner(anotherUser)
                .available(true)
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(testUser);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(itemRepository.isItemAvailable(anyLong())).thenReturn(true);
        when(bookingRepository.save(any())).thenReturn(testBooking);

        BookingDto result = bookingService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testBooking.getId());
        verify(bookingRepository).save(any());
    }

    @Test
    void updateBooking_shouldThrowExceptionWhenUserNotFound() {
        when(userService.getAuthenticatedUser()).thenReturn(testUser);

        assertThrows(IdNotFoundException.class,
                () -> bookingService.updateBooking(1L, true));
    }

    @Test
    void updateBooking_shouldThrowExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userService.getAuthenticatedUser()).thenReturn(testUser);

        assertThrows(IdNotFoundException.class,
                () -> bookingService.updateBooking(1L, true));
    }

    @Test
    void updateBooking_shouldThrowExceptionWhenStatusAlreadyApproved() {
        Booking approvedBooking = testBooking.toBuilder()
                .status(BookingStatus.APPROVED.name())
                .build();


        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(approvedBooking));

        assertThrows(ValidateException.class,
                () -> bookingService.updateBooking(1L, true));
    }

    @Test
    void updateBooking_shouldUpdateStatusToApproved() {
        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));

        BookingDto result = bookingService.updateBooking(1L, true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING.name());
    }

    @Test
    void getBooking_shouldReturnBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));

        when(userService.getAuthenticatedUser()).thenReturn(testUser);

        BookingDto result = bookingService.getBooking(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testBooking.getId());
    }

    @Test
    void getBookingsByUser_shouldReturnBookings() {
        when(bookingRepository.findBookingByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(testBooking));

        when(userService.getAuthenticatedUser()).thenReturn(testUser);

        Pageable pageable = PageRequest.of(0, 10);
        List<BookingDto> result = bookingService.getBookingsByUser(RequestBookingStatus.ALL, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testBooking.getId());
    }

    @Test
    @WithMockUser
    void getBookingStatusByOwner_shouldReturnBookings() {
        when(itemRepository.findItemsByOwnerId(anyLong())).thenReturn(List.of(testItem));
        when(bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(testBooking));


        when(userService.getAuthenticatedUser()).thenReturn(testUser);

        Pageable pageable = PageRequest.of(0, 10);
        List<BookingDto> result = bookingService.getBookingStatusByOwner(RequestBookingStatus.ALL, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testBooking.getId());
    }
}