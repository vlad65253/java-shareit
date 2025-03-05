package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final HashMap<Long, Item> items = new HashMap<>();
    private final HashMap<Long, List<Item>> itemsByOwner = new HashMap<>();

    public Item createItem(Long id, Item item) {
        items.put(id, item);
        item.setId(id);
        long ownerId = item.getOwner().getId();
        if (!itemsByOwner.containsKey(ownerId)) {
            itemsByOwner.put(ownerId, new ArrayList<>());
        }
        itemsByOwner.get(ownerId).add(item);
        return item;
    }

    public Optional<Item> getItem(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public List<Item> findAllByNameLikeOrDescriptionLike(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().equalsIgnoreCase(text) || item.getDescription().equalsIgnoreCase(text)) && item.isAvailable())
                .toList();
    }

    public List<Item> findAllByOwnerId(long userId) {
        return itemsByOwner.get(userId);
    }
}
