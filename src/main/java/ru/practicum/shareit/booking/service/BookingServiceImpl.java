package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final EntityManager entityManager;

    @Override
    public BookingDto create(Long bookerId, BookingUpdateDto bookingParam) {
        validateDate(bookingParam);
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + bookerId + " не найден"));

        Item item = itemRepository.findById(bookingParam.getItemId())
                .orElseThrow(() -> new IdNotFoundException("Вещь с id = " + bookerId + " не найдена"));

        if (bookerId.equals(item.getOwner().getId())) {
            throw new IdNotFoundException("Пользователь с id = " + bookerId + " не найден");
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
    public BookingDto updateBooking(Long ownerId, Long bookingId, Boolean isApproved) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + ownerId + " не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Бронирования с id = " + bookingId + " не найдено"));

        if (BookingStatus.valueOf(String.valueOf(booking.getStatus())).equals(BookingStatus.APPROVED)) {
            throw new ValidateException();
        }
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new IdNotFoundException("Вещь с id = " + booking.getItem().getId() + " не найдена"));

        if (!ownerId.equals(item.getOwner().getId())) {
            throw new IdNotFoundException("Пользователь с id = " + ownerId + " не найден");
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
    public BookingDto getBooking(Long userId, Long bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Бронирования с id = " + bookingId + " не найдено"));

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new IdNotFoundException("Вещь с id = " + booking.getItem().getId() + " не найдена"));

        if (!userId.equals(item.getOwner().getId())
                && !userId.equals(booking.getBooker().getId())) {
            throw new IdNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return BookingMapper.bookingToBookingDTO(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long bookerId, RequestBookingStatus state) {
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + bookerId + " не найден"));

        List<Booking> result = findBookingsByUserIdAndStatus(bookerId, state);
        return result.stream()
                .map(BookingMapper::bookingToBookingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingStatusByOwner(Long ownerId, RequestBookingStatus state) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + ownerId + " не найден"));

        List<Item> items = itemRepository.findItemsByOwnerId(ownerId);
        if (items.isEmpty()) {
            throw new IdNotFoundException("Вещь с id пользователя = " + ownerId + " не найдена");
        }
        return findBookingsByOwnerIdAndStatus(ownerId, state).stream()
                .map(BookingMapper::bookingToBookingDTO)
                .collect(Collectors.toList());
    }

    private List<Booking> findBookingsByOwnerIdAndStatus(Long ownerId, RequestBookingStatus state) {
        switch (state) {
            case ALL:
                return bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(ownerId);
            case WAITING:
                return bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.WAITING.name());
            case REJECTED:
                return bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.REJECTED.name());
            case CURRENT:
                return bookingRepository.findCurrentBookingByOwnerId(ownerId);
            case PAST:
                return bookingRepository.findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId,
                        LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId,
                        LocalDateTime.now());
            default:
                throw new RequestStatusException(state.name());
        }
    }

    private List<Booking> findBookingsByUserIdAndStatus(Long bookerId, RequestBookingStatus state) {
        switch (state) {
            case ALL:
                return bookingRepository.findBookingByBookerIdOrderByStartDesc(bookerId);
            case WAITING:
                return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.WAITING.name());
            case REJECTED:
                return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.REJECTED.name());
            case CURRENT:
                return bookingRepository.findCurrentBookingByBookerId(bookerId);
            case PAST:
                return bookingRepository.findPastBookingByBookerId(bookerId);
            case FUTURE:
                return bookingRepository.findFutureBookingByBookerId(bookerId);
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
