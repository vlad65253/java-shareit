package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.interfaces.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader(HEADER_USER_ID) long userId) {
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(HEADER_USER_ID) long userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsOtherUser(@RequestHeader(HEADER_USER_ID) long userId) {
        return itemRequestService.getRequestsOtherUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestsById(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
