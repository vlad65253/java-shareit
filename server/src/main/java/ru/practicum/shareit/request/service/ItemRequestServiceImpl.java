package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.interfaces.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId) {
        validateUser(userId);
        ItemRequest request = ItemRequestMapper.toItemRequest(itemRequestDto, validateUser(userId));
        request.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getRequests(long userId) {
        validateUser(userId);
        return itemRequestRepository.findAllByCreatedUserId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getRequestsOtherUser(long userId) {
        validateUser(userId);
        return itemRequestRepository.findAllByCreatedUserIdIsNot(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        validateUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    public User validateUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
