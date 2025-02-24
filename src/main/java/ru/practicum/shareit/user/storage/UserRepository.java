package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
@Repository
public class UserRepository {
    HashMap<Long, User> users = new HashMap<>();

    public User createUser(long id, User user) {
        users.put(id, user);
        user.setId(id);
        return user;
    }

    public List<User> getUsers() {
        return users.values().stream().toList();
    }
}
