package com.example.bookreview.service;

import com.example.bookreview.model.Review;
import com.example.bookreview.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // Inject repository via constructor
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // Business Logic: Submit a new review (Main Scenario Steps 3 & 4)
    public Review submitReview(Review review) {
        // **Precondition Check Placeholder:**
        // In a real app, you'd check:
        // if (!studentHasBorrowedOrRead(review.getStudentId(), review.getBookId())) {
        //     throw new IllegalStateException("Student hasn't read or borrowed this book.");
        // }

        // Initial review is not approved, pending moderation
        review.setApproved(false);
        return reviewRepository.save(review);
    }

    // Read: Get all approved reviews for display (Main Scenario Step 5)
    public List<Review> getApprovedReviews(Long bookId) {
        return reviewRepository.findByBookId(bookId).stream()
                .filter(Review::isApproved)
                .toList();
    }

    // Admin Read: Get all reviews pending approval
    public List<Review> getPendingReviews() {
        return reviewRepository.findByIsApprovedFalse();
    }

    // Admin CRUD: Delete a review
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    // Admin CRUD: Approve a review (Moderation)
    public Optional<Review> approveReview(Long reviewId) {
        return reviewRepository.findById(reviewId).map(review -> {
            review.setApproved(true);
            return reviewRepository.save(review);
        });
    }

    // Admin CRUD: Get review by ID
    public Optional<Review> getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    // Admin CRUD: Update a review
    public void updateReview(Long reviewId, Review updatedReview) {
        reviewRepository.findById(reviewId).ifPresent(review -> {
            review.setContent(updatedReview.getContent());
            review.setRating(updatedReview.getRating());
            reviewRepository.save(review);
        });
    }

    // User CRUD: Get reviews by user ID
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByStudentId(userId);
    }

    // Analytics: Get average rating for a book
    public Double getAverageRating(Long bookId) {
        List<Review> approvedReviews = getApprovedReviews(bookId);
        if (approvedReviews.isEmpty()) {
            return 0.0;
        }
        return approvedReviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    // Analytics: Get review count for a book
    public Long getReviewCount(Long bookId) {
        return (long) getApprovedReviews(bookId).size();
    }

    // Admin: Get all reviews (both approved and pending)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}