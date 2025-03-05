package ru.practicum.shareit.user.storage;

import jakarta.validation.ValidationException;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Getter
public class UserRepository {
    private HashMap<Long, User> users = new HashMap<>();
    private final Set<String> emails = new LinkedHashSet<>();

    public User createUser(long id, User user) {
        if(emails.contains(user.getEmail())){
            throw new ValidationException("Email должен быть уникальным");
        }
        emails.add(user.getEmail());
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
            emails.remove(users.get(id).getEmail());
            emails.add(user.getEmail());
            tempUser.setEmail(user.getEmail());
        }
        users.put(id, tempUser);
        return users.get(id);
    }

    public User getUser(long id) {
        return users.get(id);
    }

    public void deleteUser(long userId) {
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }
}
