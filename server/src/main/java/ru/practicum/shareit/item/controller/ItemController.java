package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.service.interfaces.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(HEADER_USER_ID) long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestHeader(HEADER_USER_ID) long userId,
                                 @RequestBody NewCommentDto newCommentDto) {
        return itemService.addComment(userId, itemId, newCommentDto.getText());
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto,
                             @RequestHeader(HEADER_USER_ID) long userId,
                             @PathVariable long itemId) {
        return itemService.patchItem(itemId, itemDto, userId);
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


}
