package ru.practicum.shareit.request.service.interfaces;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getRequests(long userId);

    List<ItemRequestDto> getRequestsOtherUser(long userId);

    ItemRequestDto getRequestById(long userId, long requestId);
}
