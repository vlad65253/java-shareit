package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private long id = 0;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.createItem(generateId(), item));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.getItem(itemId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.findAllByNameLikeOrDescriptionLike(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto patchItem(Long itemId, ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.getItem(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (item.getOwner().getId() != userId) {
            throw new ValidationException("Вы не владелец этой вещи");
        }
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDto(itemRepository.createItem(itemId, item));
    }

    private long generateId() {
        return id++;
    }
}
