package com.example.bookreview.controller;

import com.example.bookreview.model.Book;
import com.example.bookreview.repository.BookRepository;
import com.example.bookreview.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final ReviewService reviewService;

    public BookController(BookRepository bookRepository, ReviewService reviewService) {
        this.bookRepository = bookRepository;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String listBooks(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        return "book-list";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            // Get approved reviews for this book
            model.addAttribute("reviews", reviewService.getApprovedReviews(id));
            // Add review analytics
            model.addAttribute("averageRating", reviewService.getAverageRating(id));
            model.addAttribute("reviewCount", reviewService.getReviewCount(id));
            return "book-detail";
        } else {
            return "redirect:/books";
        }
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false) String query, Model model) {
        List<Book> books;
        if (query != null && !query.trim().isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCase(query);
            if (books.isEmpty()) {
                books = bookRepository.findByAuthorContainingIgnoreCase(query);
            }
        } else {
            books = bookRepository.findAll();
        }
        model.addAttribute("books", books);
        model.addAttribute("query", query);
        return "book-list";
    }
}
