package ru.practicum.shareit.item.service.interfaces;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto getItem(Long itemId);

    ItemDto patchItem(Long itemId, ItemDto itemDto, long userId);

    List<ItemDto> getItems(long userId);

    List<ItemDto> searchItems(String text, long userId);
}
