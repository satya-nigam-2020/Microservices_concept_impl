package com.example.controller;

import com.example.exceptions.AccessDeniedException;
import com.example.exceptions.UserNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getUser")
    public ResponseEntity<User> getUser() {
        // Implementation to get user details
        // For demonstration purposes, let's assume the logged-in user can access their own details
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/createUser")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Implementation to create a new user
        // For demonstration purposes, let's assume the logged-in user must have the "ADMIN" role to create a user
        System.out.println("At line 37");

        Collection<? extends GrantedAuthority> loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        System.out.println("At line 40 "+loggedInUsername.toArray());
        boolean hasAdminRole = loggedInUsername.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        System.out.println("At line 43 "+hasAdminRole);
        /*User loggedInUser = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));*/

        if (!hasAdminRole) {
            System.out.println("Inside exception block");
            throw new AccessDeniedException("Access denied");
        }

        User createdUser = userRepository.save(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/loginUser")
    public ResponseEntity<String> loginUser() {
        // Implementation for login endpoint
        // This endpoint does not require authentication due to the 'permitAll' configuration in SecurityConfig
        return ResponseEntity.ok("Login successful!");
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}
