package com.finance.repository;

import com.finance.model.User;
import java.util.*;

public class UserRepository {
    private Map<String, User> users;
    private static UserRepository instance;

    private UserRepository() {
        this.users = new HashMap<>();
        // Add some test users
        users.put("user1", new User("user1", "password1"));
        users.put("user2", new User("user2", "password2"));
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public boolean authenticate(String username, String password) {
        return findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // For testing - clear all users
    public void clear() {
        users.clear();
        // Re-add test users
        users.put("user1", new User("user1", "password1"));
        users.put("user2", new User("user2", "password2"));
    }
}