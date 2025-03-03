package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepository {
    private HashMap<Long, User> users = new HashMap<>();

    public User createUser(long id, User user) {
        users.put(id, user);
        user.setId(id);
        return user;
    }

    public List<User> getUsers() {
        return users.values().stream().toList();
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User save(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public User patchUser(long id, User user) {
        User tempUser = users.get(id);
        if (user.getName() != null) {
            tempUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            tempUser.setEmail(user.getEmail());
        }
        users.put(id, tempUser);
        return users.get(id);
    }

    public User getUser(long id) {
        return users.get(id);
    }

    public void deleteUser(long userId) {
        users.remove(userId);
    }
}
