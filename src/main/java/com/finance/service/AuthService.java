package com.finance.service;

import com.finance.model.User;
import com.finance.repository.UserRepository;
import com.finance.repository.DataStorage;
import java.util.List;
import java.util.Optional;

public class AuthService {
    private UserRepository userRepository;
    private DataStorage dataStorage;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.dataStorage = new DataStorage();
        loadUsersFromStorage();
    }

    private void loadUsersFromStorage() {
        List<User> users = dataStorage.loadUsers();
        if (users != null) {
            for (User user : users) {
                userRepository.addUser(user);
            }
        }
    }

    public boolean login(String username, String password) {
        if (userRepository.authenticate(username, password)) {
            currentUser = userRepository.findByUsername(username).get();
            return true;
        }
        return false;
    }

    public void logout() {
        saveCurrentUserData();
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void saveCurrentUserData() {
        if (currentUser != null) {
            dataStorage.saveUsers(userRepository.getAllUsers());
        }
    }

    // For testing
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}