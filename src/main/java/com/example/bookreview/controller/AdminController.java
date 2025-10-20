package com.example.bookreview.controller;

import com.example.bookreview.service.ReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ReviewService reviewService;

    public AdminController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboard(Model model) {
        model.addAttribute("pendingReviews", reviewService.getPendingReviews());
        model.addAttribute("allReviews", reviewService.getAllReviews());
        return "admin-dashboard";
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasRole('ADMIN')")
    public String reviews(Model model) {
        model.addAttribute("pendingReviews", reviewService.getPendingReviews());
        model.addAttribute("allReviews", reviewService.getAllReviews());
        return "admin-dashboard";
    }

    @GetMapping("/reviews/all")
    @PreAuthorize("hasRole('ADMIN')")
    public String allReviews(Model model) {
        model.addAttribute("allReviews", reviewService.getAllReviews());
        model.addAttribute("pendingReviews", reviewService.getPendingReviews());
        return "admin-dashboard";
    }
}
