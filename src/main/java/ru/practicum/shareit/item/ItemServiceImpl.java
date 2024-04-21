package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.debug("Вещь создана");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        return ItemMapper.itemToItemDto(itemRepository.save(ItemMapper.itemDtoToItem(itemDto, user)));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        log.debug("Вещь обновлена");
        Item item = itemRepository.getById(itemId);
        if (!userId.equals(item.getOwner().getId())) {
            throw new IdNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.itemToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemFullDto getByIdItem(Long userId, Long itemId) {
        log.debug("Вещь с id = {} получена", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Вещь с id = " + itemId + " не найдена"));

        List<Booking> bookingList = bookingRepository.findBookingsByItemId(item.getId());
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (bookingList.size() == 1) {
            lastBooking = getNextBooking(bookingList);
        } else if (bookingList.stream().map(Booking::getBooker)
                .map(User::getId)
                .noneMatch(it -> it.equals(userId))
        ) {
            lastBooking = getLastBooking(bookingList);
            nextBooking = getNextBooking(bookingList);

        }
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDto> commentsDTO = comments.stream()
                .map(CommentMapper::commentToCommentDTO)
                .collect(Collectors.toList());
        return ItemMapper.itemToItemFullDTO(item, commentsDTO, lastBooking, nextBooking);
    }

    @Override
    public List<ItemFullDto> findAllItemsByOwnerId(Long userId) {
        List<Item> items = itemRepository.findItemsByOwnerId(userId);
        return items.stream()
                .map(item -> getByIdItem(userId, item.getId()))
                .sorted(Comparator.comparing(ItemFullDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.debug("Подходящие вещи найдены");
        if (text.isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .distinct()
                .map(ItemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto makeComment(Long userId, Long itemId, CommentUpdateDto text) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Вещь с id = " + itemId + " не найдена"));

        if (!bookingRepository.existsBookingByBookerIdAndStatus(userId,
                BookingStatus.APPROVED.name())) {
            throw new ValidateException();
        }
        Comment comment = commentRepository.save(Comment.builder()
                .text(text.getText())
                .item(item)
                .user(user)
                .build());
        return CommentMapper.commentToCommentDTO(comment);
    }

    private Booking getNextBooking(List<Booking> bookingList) {
        return bookingList.stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED.name()))
                .sorted(Comparator.comparing(Booking::getEnd))
                .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

    private Booking getLastBooking(List<Booking> bookingList) {
        return bookingList.stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED.name()))
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
}
