package com.example.bookreview.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.bookreview.model.Book;
import com.example.bookreview.model.User;
import com.example.bookreview.repository.BookRepository;
import com.example.bookreview.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(BookRepository bookRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Add admin user if it doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("it.admin@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("123456"));
            adminUser.setRole(User.Role.ADMIN);
            adminUser.setEnabled(true);
            userRepository.save(adminUser);
            System.out.println("Admin user created: admin / 123456");
        }

        // Only add sample data if no books exist
        if (bookRepository.count() == 0) {
            // Sample books
            Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", 
                "A classic American novel set in the Jazz Age, following the mysterious Jay Gatsby and his obsession with the beautiful Daisy Buchanan.", 
                "978-0-7432-7356-5");
            
            Book book2 = new Book("To Kill a Mockingbird", "Harper Lee", 
                "A gripping tale of racial injustice and childhood innocence in the American South during the 1930s.", 
                "978-0-06-112008-4");
            
            Book book3 = new Book("1984", "George Orwell", 
                "A dystopian social science fiction novel about totalitarian control and surveillance in a future society.", 
                "978-0-452-28423-4");
            
            Book book4 = new Book("Pride and Prejudice", "Jane Austen", 
                "A romantic novel of manners that critiques the British landed gentry at the turn of the 19th century.", 
                "978-0-14-143951-8");
            
            Book book5 = new Book("The Catcher in the Rye", "J.D. Salinger", 
                "A coming-of-age story about teenage rebellion and alienation in 1950s America.", 
                "978-0-316-76948-0");

            bookRepository.save(book1);
            bookRepository.save(book2);
            bookRepository.save(book3);
            bookRepository.save(book4);
            bookRepository.save(book5);
            
            System.out.println("Sample books have been added to the database.");
        }
    }
}
