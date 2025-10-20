package com.example.bookreview.repository;

import com.example.bookreview.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Custom query method to fetch reviews for a specific book
    List<Review> findByBookId(Long bookId);

    // Custom query for Admin moderation view
    List<Review> findByIsApprovedFalse();

    // Custom query to fetch reviews by student ID
    List<Review> findByStudentId(Long studentId);
}