package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.interfaces.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HEADER_USER_ID) long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(HEADER_USER_ID) long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = false) String text,
                                     @RequestHeader(HEADER_USER_ID) long userId) {
        return itemService.searchItems(text, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto, @RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId) {
        return itemService.patchItem(itemId, itemDto, userId);
    }
}
