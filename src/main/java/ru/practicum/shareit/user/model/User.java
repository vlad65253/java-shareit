package ru.practicum.shareit.user.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private List<Item> itemsForUser = new ArrayList<>();
}
