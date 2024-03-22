package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.debug("Вещь создана");
        userRepository.getById(userId);
        Item item = itemDtoToItem(itemDto);
        Item addItem = itemRepository.create(userId, item);
        return itemToItemDto(addItem);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        log.debug("Вещь обновлена");
        Item item = itemDtoToItem(itemDto);
        Item updateItem = itemRepository.update(userId, item, itemId);
        return itemToItemDto(updateItem);
    }

    @Override
    public ItemDto getByIdItem(Long itemId) {
        log.debug("Вещь с id = {} получена", itemId);
        Item item = itemRepository.getById(itemId);
        return itemToItemDto(item);
    }

    @Override
    public List<ItemDto> findAllItemsByOwnerId(Long userId) {
        List<Item> users = itemRepository.findAllItemsByOwnerId(userId);
        return users.stream()
                .map(this::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.debug("Подходящие вещи найдены");
        List<Item> items = itemRepository.search(text);
        return items.stream()
                .map(this::itemToItemDto)
                .collect(Collectors.toList());
    }

    private Item itemDtoToItem(ItemDto itemDto) {

        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    private ItemDto itemToItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
