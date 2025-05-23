package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<Item> items;

}
