package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item create(Long userId, Item item) {
        item.setId(generatorId());
        item.setOwnerId(userId);
        items.put(item.getId(), item);

        return item;

    }

    @Override
    public Item update(Long userId, Item item, Long itemId) {

        if (!items.containsKey(itemId)) {
            throw new IdNotFoundException("Вещи с id = " + itemId + "не существует");
        }
        Item updateItem = items.get(itemId);
        checkOwner(userId, updateItem);

        if (item.getName() != null && !item.getName().isBlank()) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        items.put(updateItem.getId(), updateItem);

        return updateItem;

    }

    @Override
    public Item getById(Long itemId) {

        if (!items.containsKey(itemId)) {
            throw new IdNotFoundException("Вещи с id = " + itemId + "не существует");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllItemsByOwnerId(Long userId) {

        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        String textToLowerCase = text.toLowerCase();
        if (text.isBlank()) {
            return List.of();
        }

        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(textToLowerCase)
                        || item.getDescription().toLowerCase().contains(textToLowerCase))
                        && item.getAvailable())
                .collect(Collectors.toList());
    }

    private Long generatorId() {
        return ++id;
    }


    private void checkOwner(Long userId, Item item) {
        if (!item.getOwnerId().equals(userId)) {
            throw new NotOwnerException("Изменить вещь может только владелец");
        }
    }
}

