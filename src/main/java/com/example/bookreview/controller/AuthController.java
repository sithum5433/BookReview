package com.example.bookreview.controller;

import com.example.bookreview.model.User;
import com.example.bookreview.repository.UserRepository;
import com.example.bookreview.service.UserService;
import jakarta.servlet.http.HttpSession; // Import for session management
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus; // Import for session invalidation

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService; // Use the new service
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // --- LOGIN ---
    @GetMapping("/login") // Changed from /auth/login for simplicity
    public String showLogin() {
        return "login";
    }

    // Remove the manual login processing since Spring Security handles it
    // The login form will be processed by Spring Security automatically

    // Spring Security handles logout automatically

    // --- REGISTER ---
    @GetMapping("/register") // Changed from /auth/register
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
            return "register";
        }

        // Encode password properly
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.STUDENT);
        user.setEnabled(true);
        userRepository.save(user);

        return "redirect:/login?registered=true";
    }

    // --- USER PROFILE (NEW) ---
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login"; // Must be logged in
        }

        // Retrieve the full user object
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found in session."));

        model.addAttribute("user", user);
        return "profile"; // We will create this template
    }

    // --- EDIT USER DETAILS (NEW) ---
    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute @Valid User userDetails, HttpSession session, BindingResult result, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login"; // Must be logged in
        }

        if (result.hasErrors()) {
            // Keep the user ID to ensure we update the correct record
            userDetails.setId(userId);
            model.addAttribute("user", userDetails);
            return "profile";
        }

        try {
            // Update the user details via the service
            userService.updateUserDetails(userId, userDetails);

            // Update the session username in case it was changed
            session.setAttribute("username", userDetails.getUsername());

            return "redirect:/profile?updated=true";
        } catch (RuntimeException e) {
            // Handle unique constraint violations (username/email already exists)
            result.reject("globalError", e.getMessage());
            userDetails.setId(userId);
            model.addAttribute("user", userDetails);
            return "profile";
        }
    }
}
