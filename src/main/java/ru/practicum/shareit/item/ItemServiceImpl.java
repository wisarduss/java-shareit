package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryImpl itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.debug("Вещь создана");
        userRepository.getById(userId);
        return itemRepository.create(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        log.debug("Вещь обновлена");
        return itemRepository.update(userId, itemDto, itemId);
    }

    @Override
    public ItemDto getByIdItem(Long itemId) {
        log.debug("Вещь с id = {} получена", itemId);
        return itemRepository.getById(itemId);
    }

    @Override
    public List<ItemDto> findAllItemsByOwnerId(Long userId) {
        return itemRepository.findAllItemsByOwnerId(userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.debug("Подходящие вещи найдены");
        return itemRepository.search(text);
    }
}
