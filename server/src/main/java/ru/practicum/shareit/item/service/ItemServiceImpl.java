package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.category.model.Category;
import ru.practicum.shareit.category.repository.CategoryRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    /**
     * Метод создания карточки товара. Существует несколько сценариев.
     * 1) Пользователь сам выкладывает объявления и ждет заявок на бронирование.
     * 2) Арендодатель замечает запрос пользователя об необходимой вещи и указывает его в качестве карточки товара.
     *    После публикации объявления пользователю приходит уведомление о том, что была выложена необходимая ему вещь.
     *
     * @param itemDto Карточка товара
     * @return созданный товар
     */
    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto) {
        log.debug("Вещь создана");
        User user = userService.getAuthenticatedUser();

        Set<Category> categories = new HashSet<>();

        for (Long catId : itemDto.getCatIds()) {
            Category category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new IdNotFoundException(String.format("категория с id = %d не найдена", catId)));
            categories.add(category);
        }

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new IdNotFoundException("Запрос с id = " + itemDto.getRequestId()
                            + "не найден"));
            return ItemMapper.itemToItemDto(itemRepository.save(ItemMapper
                    .itemDtoToItemWithRequest(itemDto, categories, user, itemRequest)));
        }

        return ItemMapper.itemToItemDto(itemRepository.save(ItemMapper
                .itemDtoToItemWithoutRequest(itemDto, categories, user)));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId) {
        log.debug("Вещь обновлена");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Товар с id =" + itemId + "не найден"));
        User user = userService.getAuthenticatedUser();
        if (!user.getId().equals(item.getOwner().getId())) {
            throw new IdNotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getPhotoUrl() != null) {
            item.setPhotoUrl(itemDto.getPhotoUrl());
        }
        if (itemDto.getPrice() != null) {
            item.setPrice(itemDto.getPrice());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.itemToItemDto(itemRepository.save(item));
    }

    @Override
    public Map<String, List<ItemResponseDto>> getAll() {
        List<Item> items = itemRepository.findAll();

        Map<String, List<ItemResponseDto>> result = new HashMap<>();

        for (Item item : items) {
            ItemResponseDto itemDto = ItemMapper.itemToItemResponseDto(item);

            // Получаем список названий категорий для этого товара
            String categories = item.getCategories().stream()
                    .map(category -> getCategoryTitleById(category.getId()))
                    .collect(Collectors.joining(", ", "(", ")"));

            // Добавляем товар в результат для каждой категории
            result.computeIfAbsent(categories, k -> new ArrayList<>()).add(itemDto);
        }

        return result;
    }

    @Override
    public ItemFullDto getByIdItem(Long itemId) {
        log.debug("Вещь с id = {} получена", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Вещь с id = " + itemId + " не найдена"));

        User user = userService.getAuthenticatedUser();

        List<Booking> bookingList = bookingRepository.findBookingsByItemId(item.getId());
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (bookingList.size() == 1) {
            lastBooking = getNextBooking(bookingList);
        } else if (bookingList.stream().map(Booking::getBooker)
                .map(User::getId)
                .noneMatch(it -> it.equals(user.getId()))
        ) {
            lastBooking = getLastBooking(bookingList);
            nextBooking = getNextBooking(bookingList);

        }
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDto> commentsDTO = comments.stream()
                .map(CommentMapper::commentToCommentDTO)
                .collect(Collectors.toList());
        return ItemMapper.itemToItemFullDto(item, commentsDTO, lastBooking, nextBooking);
    }

    @Override
    public List<ItemFullDto> findAllItemsByOwnerId(Pageable pageable) {
        User user = userService.getAuthenticatedUser();

        List<Item> items = itemRepository.findItemsByOwnerId(user.getId(), pageable);
        return items.stream()
                .map(item -> getByIdItem(item.getId()))
                .sorted(Comparator.comparing(ItemFullDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text, Pageable pageable) {
        log.debug("Подходящие вещи найдены");
        if (text.isEmpty()) {
            return List.of();
        }

        return itemRepository.search(text, pageable).stream()
                .distinct()
                .map(ItemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> getItemForCatId(Long catId) {

        categoryRepository.findById(catId)
                .orElseThrow(() -> new IdNotFoundException(String
                        .format("Извините такой категории с id = %d не существует", catId)));

        List<Item> items = itemRepository.getItemForCatId(catId);

        return items.stream()
                .map(ItemMapper::itemToItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto makeComment(Long itemId, CommentUpdateDto text) {
        User user = userService.getAuthenticatedUser();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Вещь с id = " + itemId + " не найдена"));

        if (!bookingRepository.existsBookingByBookerIdAndStatus(user.getId(),
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

    private String getCategoryTitleById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        return categoryOptional.map(Category::getTitle).orElse(null);
    }
}
