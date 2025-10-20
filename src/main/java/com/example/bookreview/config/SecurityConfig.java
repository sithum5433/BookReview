package com.example.bookreview.config;

import com.example.bookreview.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Inject our custom success handler and user details service
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(@Qualifier("roleBasedAuthenticationSuccessHandler") AuthenticationSuccessHandler authenticationSuccessHandler,
                         CustomUserDetailsService customUserDetailsService) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Allow public access to static resources, home, login, register, and book pages
                        .requestMatchers("/", "/home", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login", "/register", "/auth/**").permitAll()
                        .requestMatchers("/books", "/books/{id}", "/books/search").permitAll()

                        // Secure the admin dashboard
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Secure review submission to require a user role
                        .requestMatchers("/reviews/new/**").hasRole("STUDENT")

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .userDetailsService(customUserDetailsService)
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page URL
                        .loginProcessingUrl("/login") // The URL the form should POST to
                        .successHandler(authenticationSuccessHandler) // USE OUR CUSTOM HANDLER HERE
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()) // Note: Disable CSRF for simplicity, enable in production
                .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Using database users via CustomUserDetailsService instead of in-memory users
}
