package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemRepository {
    HashMap<Long, Item> items = new HashMap<>();


    public Item createItem(Long id, Item item) {
        items.put(id, item);
        item.setId(id);
        return item;
    }

    public Optional<Item> getItem(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public List<Item> getItems() {
        return items.values().stream().toList();
    }

    public List<Item> findAllByNameLikeOrDescriptionLike(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().equalsIgnoreCase(text) || item.getDescription().equalsIgnoreCase(text)) && item.isAvailable())
                .toList();
    }

    public List<Item> findAllByOwnerId(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }
}
