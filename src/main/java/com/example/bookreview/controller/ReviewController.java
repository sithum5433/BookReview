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

import java.util.Optional;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    // STUDENT SIDE: Display form (Step 2: Selects a book)
    @GetMapping("/new/{bookId}")
    public String showReviewForm(@PathVariable Long bookId, Authentication authentication, Model model) {
        Review review = new Review();
        review.setBookId(bookId);
        // Set studentId based on logged-in user
        String username = authentication.getName();
        Long userId = userService.findByUsername(username).getId();
        review.setStudentId(userId);

        model.addAttribute("review", review);
        return "review-form";
    }

    // STUDENT SIDE: Process form submission (Steps 3 & 4: Validate and Store)
    @PostMapping("/new")
    public String submitReview(
            @Valid @ModelAttribute("review") Review review,
            BindingResult result,
            Authentication authentication,
            Model model) {

        // Step 3: System validates the input (BindingResult checks Bean Validation)
        if (result.hasErrors()) {
            // Return to form if validation fails
            return "review-form";
        }

        try {
            // Ensure the studentId is set from the logged-in user
            String username = authentication.getName();
            Long userId = userService.findByUsername(username).getId();
            review.setStudentId(userId);
            
            reviewService.submitReview(review); // Step 4: Review stored
            return "redirect:/books/" + review.getBookId(); // Redirect to book page
        } catch (IllegalStateException e) {
            // Handle Precondition failure
            model.addAttribute("error", e.getMessage());
            return "review-form";
        }
    }

    // ADMIN SIDE: View pending reviews (CRUD Control)
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewPendingReviews(Model model) {
        model.addAttribute("pendingReviews", reviewService.getPendingReviews());
        return "admin-moderation";
    }

    // ADMIN SIDE: Approve a review
    @PostMapping("/admin/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveReview(@PathVariable Long id) {
        reviewService.approveReview(id);
        return "redirect:/admin/dashboard";
    }

    // ADMIN SIDE: Delete a review
    @PostMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/admin/dashboard";
    }

    // ADMIN SIDE: Edit a review (GET)
    @GetMapping("/admin/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editReviewForm(@PathVariable Long id, Model model) {
        Optional<Review> review = reviewService.getReviewById(id);
        if (review.isPresent()) {
            model.addAttribute("review", review.get());
            return "admin-edit-review";
        }
        return "redirect:/admin/dashboard";
    }

    // ADMIN SIDE: Update a review (POST)
    @PostMapping("/admin/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateReview(@PathVariable Long id, @Valid Review review, BindingResult result) {
        if (result.hasErrors()) {
            return "admin-edit-review";
        }
        reviewService.updateReview(id, review);
        return "redirect:/admin/dashboard";
    }
}