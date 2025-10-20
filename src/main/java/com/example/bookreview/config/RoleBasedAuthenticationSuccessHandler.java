package com.example.bookreview.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Called when a user has been successfully authenticated.
     * This method checks the user's roles and redirects them to the appropriate URL.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during the
     * authentication process.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            // If the user has the 'ROLE_ADMIN', redirect to the admin dashboard.
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/dashboard");
                return; // Exit after redirecting
            }
            // If the user has the 'ROLE_STUDENT', redirect to the main book list.
            else if (authority.getAuthority().equals("ROLE_STUDENT")) {
                response.sendRedirect("/books");
                return; // Exit after redirecting
            }
        }

        // If the user has no specific role, or something unexpected happens,
        // redirect to a default page.
        response.sendRedirect("/");
    }
}
