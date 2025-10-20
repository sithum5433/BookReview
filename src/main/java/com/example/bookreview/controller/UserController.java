package com.example.bookreview.controller;

import com.example.bookreview.model.Review;
import com.example.bookreview.service.ReviewService;
import com.example.bookreview.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final ReviewService reviewService;
    private final UserService userService;

    public UserController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    // User Dashboard - View all their reviews
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public String userDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Long userId = userService.findByUsername(username).getId();
        
        List<Review> userReviews = reviewService.getReviewsByUserId(userId);
        model.addAttribute("userReviews", userReviews);
        model.addAttribute("username", username);
        
        return "user-dashboard";
    }

    // Edit user's own review
    @GetMapping("/review/edit/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public String editUserReview(@PathVariable Long id, Authentication authentication, Model model) {
        String username = authentication.getName();
        Long userId = userService.findByUsername(username).getId();
        
        Review review = reviewService.getReviewById(id).orElse(null);
        if (review == null || !review.getStudentId().equals(userId)) {
            return "redirect:/user/dashboard?error=unauthorized";
        }
        
        model.addAttribute("review", review);
        return "user-edit-review";
    }

    // Update user's own review
    @PostMapping("/review/update/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public String updateUserReview(@PathVariable Long id, @Valid Review review, 
                                 BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            return "user-edit-review";
        }
        
        String username = authentication.getName();
        Long userId = userService.findByUsername(username).getId();
        
        Review existingReview = reviewService.getReviewById(id).orElse(null);
        if (existingReview == null || !existingReview.getStudentId().equals(userId)) {
            return "redirect:/user/dashboard?error=unauthorized";
        }
        
        reviewService.updateReview(id, review);
        return "redirect:/user/dashboard?success=updated";
    }

    // Delete user's own review
    @PostMapping("/review/delete/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public String deleteUserReview(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Long userId = userService.findByUsername(username).getId();
        
        Review review = reviewService.getReviewById(id).orElse(null);
        if (review == null || !review.getStudentId().equals(userId)) {
            return "redirect:/user/dashboard?error=unauthorized";
        }
        
        reviewService.deleteReview(id);
        return "redirect:/user/dashboard?success=deleted";
    }
}
