package com.example.bookreview.service;

import com.example.bookreview.model.User;
import com.example.bookreview.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void updateUserDetails(Long userId, User userDetails) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if username is being changed and if it already exists
        if (!existingUser.getUsername().equals(userDetails.getUsername()) 
            && userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email is being changed and if it already exists
        if (!existingUser.getEmail().equals(userDetails.getEmail()) 
            && userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Update the user details
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setPassword(userDetails.getPassword()); // In production, encode this

        userRepository.save(existingUser);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}