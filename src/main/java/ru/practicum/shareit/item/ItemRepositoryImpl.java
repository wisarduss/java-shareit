package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {

        Item item = itemDtoToItem(itemDto);
        item.setId(generatorId());
        item.setOwnerId(userId);
        items.put(item.getId(), item);

        return itemToItemDto(item);

    }

    @Override
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {

        if (!items.containsKey(itemId)) {
            throw new IdNotFoundException("Вещи с id = " + itemId + "не существует");
        }
        Item item = items.get(itemId);
        checkOwner(userId, item);

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.put(item.getId(), item);

        return itemToItemDto(item);

    }

    @Override
    public ItemDto getById(Long itemId) {

        if (!items.containsKey(itemId)) {
            throw new IdNotFoundException("Вещи с id = " + itemId + "не существует");
        }
        Item item = items.get(itemId);
        return itemToItemDto(item);
    }

    @Override
    public List<ItemDto> findAllItemsByOwnerId(Long userId) {

        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .map(this::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable())
                .map(this::itemToItemDto)
                .collect(Collectors.toList());
    }

    private Long generatorId() {
        return ++id;
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

    private void checkOwner(Long userId, Item item) {
        if (!item.getOwnerId().equals(userId)) {
            throw new NotOwnerException("Изменить вещь может только владелец");
        }
    }
}

