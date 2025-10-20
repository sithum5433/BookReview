package com.example.bookreview.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A review must be linked to a Book and a User (Student)
    // Assume Book and User are defined, using simple Longs for now
    private Long bookId;
    private Long studentId;

    @NotNull(message = "Rating is required.")
    @Min(value = 1, message = "Rating must be at least 1 star.")
    @Max(value = 5, message = "Rating cannot exceed 5 stars.")
    private Integer rating; // 1 to 5 stars

    @NotBlank(message = "Review text cannot be empty.")
    @Size(min = 10, max = 500, message = "Review must be between 10 and 500 characters.")
    @Column(length = 500)
    private String content;

    private boolean isApproved = false; // For Admin moderation

    // Constructors
    public Review() {}

    public Review(Long bookId, Long studentId, Integer rating, String content) {
        this.bookId = bookId;
        this.studentId = studentId;
        this.rating = rating;
        this.content = content;
        this.isApproved = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}