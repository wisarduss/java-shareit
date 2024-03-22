package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Long userId, Item item);

    Item update(Long userId, Item item, Long itemId);

    Item getById(Long itemId);

    List<Item> findAllItemsByOwnerId(Long userId);

    List<Item> search(String text);
}
