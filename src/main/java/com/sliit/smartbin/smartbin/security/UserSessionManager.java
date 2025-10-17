package com.sliit.smartbin.smartbin.security;

import com.sliit.smartbin.smartbin.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * CODE SMELL FIX: Primitive Obsession & Duplicate Code
 * 
 * BEFORE: Every controller had duplicate session.getAttribute("user") code
 * AFTER: Centralized user session management in single class
 * 
 * SOLID PRINCIPLE: Single Responsibility Principle (SRP)
 * - This class has ONE job: Manage user sessions
 * 
 * Benefits:
 * - Eliminates duplicate code across controllers
 * - Type-safe user retrieval (no casting needed)
 * - Easier to test and modify session handling logic
 * - Encapsulates session attribute key as constant
 */
@Component
public class UserSessionManager {
    
    private static final String USER_SESSION_KEY = "user";
    private static final String USER_ROLE_SESSION_KEY = "userRole";
    
    /**
     * Get currently logged-in user from session
     * 
     * CODE SMELL FIX: Encapsulates session.getAttribute("user") to avoid primitive obsession
     * 
     * @param session HTTP session
     * @return Optional containing user if logged in, empty otherwise
     */
    public Optional<User> getCurrentUser(HttpSession session) {
        if (session == null) {
            return Optional.empty();
        }
        Object userObj = session.getAttribute(USER_SESSION_KEY);
        return userObj instanceof User ? Optional.of((User) userObj) : Optional.empty();
    }
    
    /**
     * Validate user is logged in and has specific role
     * 
     * CODE SMELL FIX: Eliminates duplicate validation logic across controllers
     * 
     * @param session HTTP session
     * @param expectedRole Expected user role
     * @return Optional containing user if valid, empty otherwise
     */
    public Optional<User> validateUserRole(HttpSession session, User.UserRole expectedRole) {
        return getCurrentUser(session)
                .filter(user -> user.getRole() == expectedRole);
    }
    
    /**
     * Set user in session
     * 
     * @param session HTTP session
     * @param user User to store
     */
    public void setCurrentUser(HttpSession session, User user) {
        if (session != null && user != null) {
            session.setAttribute(USER_SESSION_KEY, user);
            session.setAttribute(USER_ROLE_SESSION_KEY, user.getRole().name());
        }
    }
    
    /**
     * Clear user session (logout)
     * 
     * @param session HTTP session
     */
    public void clearSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
    
    /**
     * Check if user is authenticated
     * 
     * @param session HTTP session
     * @return true if user is logged in
     */
    public boolean isAuthenticated(HttpSession session) {
        return getCurrentUser(session).isPresent();
    }
}

