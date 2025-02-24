package com.team18.MBC.Controllers;

import com.team18.MBC.Services.ImageService;
import com.team18.MBC.Services.UserService;
import com.team18.MBC.core.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController  // Converts controller into a REST API (returns JSON)
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public UserController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    // Get the logged-in user's profile
    @GetMapping("/profile")
    public ResponseEntity<?> getLoggedInUserProfile(HttpSession session) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser == null) {
            return ResponseEntity.status(403).body(Map.of("error", "User not logged in"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("user", sessionUser);
        response.put("isOwnProfile", true);

        Optional<Image> profileImage = userService.getProfileImageForUser(sessionUser.getID());
        profileImage.ifPresent(image -> response.put("profileImage", image));

        return ResponseEntity.ok(response);
    }

    // User signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        User exists = userService.findByUsername(user.getUsername());
        if (exists != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }

        userService.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    // User login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpSession session) {
        User authenticatedUser = userService.login(user);
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        session.setAttribute("LoggedInUser", authenticatedUser);
        return ResponseEntity.ok(Map.of("message", "Login successful", "user", authenticatedUser));
    }

    // Check if user is logged in
    @GetMapping("/loggedin")
    public ResponseEntity<?> isLoggedIn(HttpSession session) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            return ResponseEntity.ok(sessionUser);
        }
        return ResponseEntity.status(403).body(Map.of("error", "User not logged in"));
    }

    // Delete a user
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username, HttpSession session) {
        User userToDelete = userService.findByUsername(username);
        if (userToDelete == null) {
            return ResponseEntity.notFound().build();
        }

        userService.delete(userToDelete);
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // Update password
    @PatchMapping("/{id}/update-password")
    public ResponseEntity<?> updatePassword(
            @PathVariable("id") Long id,
            @RequestBody PasswordChangeRequest passwordChangeRequest
    ) {
        User user = userService.findUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userService.updatePassword(user, passwordChangeRequest.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    // Logout user
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    // Get user settings
    @GetMapping("/settings")
    public ResponseEntity<?> getUserSettings(HttpSession session) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser == null) {
            return ResponseEntity.status(403).body(Map.of("error", "User not logged in"));
        }
        return ResponseEntity.ok(Map.of("user", sessionUser));
    }
}
