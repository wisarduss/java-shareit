package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.RequestStatusException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final EntityManager entityManager;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto create(BookingUpdateDto bookingParam) {
        validateDate(bookingParam);

        User user = userService.getAuthenticatedUser();

        Item item = itemRepository.findById(bookingParam.getItemId())
                .orElseThrow(() -> new IdNotFoundException("Вещь с id = " + bookingParam.getItemId() + " не найдена"));

        if (user.getId().equals(item.getOwner().getId())) {
            throw new IdNotFoundException("Пользователь с id = " + user.getId() + " не может забронировать свой же товар");
        }
        if (!itemRepository.isItemAvailable(bookingParam.getItemId())) {
            throw new ItemNotAvailableException(bookingParam.getItemId().toString());
        }

        bookingParam.setStatus(BookingStatus.WAITING.name());

        Booking booking = bookingRepository.save(
                BookingMapper.bookingDtoToBooking(bookingParam, user, item));
        return BookingMapper.bookingToBookingDTO(booking);
    }


    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, Boolean isApproved) {
        User user = userService.getAuthenticatedUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Бронирования с id = " + bookingId + " не найдено"));

        if (BookingStatus.valueOf(String.valueOf(booking.getStatus())).equals(BookingStatus.APPROVED)) {
            throw new ValidateException();
        }
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new IdNotFoundException("Вещь с id = " + booking.getItem().getId() + " не найдена"));

        if (!user.getId().equals(item.getOwner().getId())) {
            throw new IdNotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        if (isApproved) {
            itemRepository.updateItemAvailableById(item.getId(), isApproved);
            bookingRepository.updateBookingStatusById(bookingId, BookingStatus.APPROVED.name());
        } else {
            bookingRepository.updateBookingStatusById(bookingId, BookingStatus.REJECTED.name());
        }
        var result = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Бронирования с id = " + bookingId + " не найдено"));

        entityManager.refresh(result);
        return BookingMapper.bookingToBookingDTO(result);
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        User user = userService.getAuthenticatedUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Бронирования с id = " + bookingId + " не найдено"));

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new IdNotFoundException("Вещь с id = " + booking.getItem().getId() + " не найдена"));

        if (!user.getId().equals(item.getOwner().getId())
                && !user.getId().equals(booking.getBooker().getId())) {
            throw new IdNotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        return BookingMapper.bookingToBookingDTO(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(RequestBookingStatus state, Pageable pageable) {
        User user = userService.getAuthenticatedUser();

        List<Booking> result = findBookingsByUserIdAndStatus(user.getId(), state, pageable);
        return result.stream()
                .map(BookingMapper::bookingToBookingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingStatusByOwner(RequestBookingStatus state, Pageable pageable) {
        User user = userService.getAuthenticatedUser();

        List<Item> items = itemRepository.findItemsByOwnerId(user.getId());
        if (items.isEmpty()) {
            throw new IdNotFoundException("Вещь с id пользователя = " + user.getId() + " не найдена");
        }
        return findBookingsByOwnerIdAndStatus(user.getId(), state, pageable).stream()
                .map(BookingMapper::bookingToBookingDTO)
                .collect(Collectors.toList());
    }

    private List<Booking> findBookingsByOwnerIdAndStatus(Long ownerId, RequestBookingStatus state,
                                                         Pageable pageable) {
        switch (state) {
            case ALL:
                return bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(ownerId, pageable);
            case WAITING:
                return bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.WAITING.name(), pageable);
            case REJECTED:
                return bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.REJECTED.name(), pageable);
            case CURRENT:
                return bookingRepository.findCurrentBookingByOwnerId(ownerId, pageable);
            case PAST:
                return bookingRepository.findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId,
                        LocalDateTime.now(), pageable);
            case FUTURE:
                return bookingRepository.findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId,
                        LocalDateTime.now(), pageable);
            default:
                throw new RequestStatusException(state.name());
        }
    }

    private List<Booking> findBookingsByUserIdAndStatus(Long bookerId, RequestBookingStatus state,
                                                        Pageable pageable) {
        switch (state) {
            case ALL:
                return bookingRepository.findBookingByBookerIdOrderByStartDesc(bookerId, pageable);
            case WAITING:
                return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.WAITING.name(), pageable);
            case REJECTED:
                return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.REJECTED.name(), pageable);
            case CURRENT:
                return bookingRepository.findCurrentBookingByBookerId(bookerId, pageable);
            case PAST:
                return bookingRepository.findPastBookingByBookerId(bookerId, pageable);
            case FUTURE:
                return bookingRepository.findFutureBookingByBookerId(bookerId, pageable);
            default:
                throw new RequestStatusException(state.name());
        }
    }

    private void validateDate(BookingUpdateDto bookingParam) {
        if (bookingParam.getStart().equals(bookingParam.getEnd())
                || bookingParam.getStart().isAfter(bookingParam.getEnd())
        ) {
            throw new ValidateException();
        }
    }
}
