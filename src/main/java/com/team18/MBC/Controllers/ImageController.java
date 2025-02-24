package com.team18.MBC.Controllers;

import com.team18.MBC.Repositories.ImageRepository;
import com.team18.MBC.Services.ImageService;
import com.team18.MBC.Services.UserService;
import com.team18.MBC.core.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController  // Converts the controller into a REST API (returns JSON)
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    // Upload an image and associate it with a user
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        User user = (User) session.getAttribute("LoggedInUser");
        if (user == null) {
            return ResponseEntity.status(403).body(Map.of("error", "User not logged in"));
        }

        try {
            imageService.saveImage(file, user);
            return ResponseEntity.ok(Map.of("message", "Profile picture uploaded successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error uploading image: " + e.getMessage()));
        }
    }

    // Get an image by ID (returns raw image data)
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(image -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")  // Adjust MIME type as needed
                        .body(image.getData()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Get a list of all images (returns metadata, not raw images)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listImages() {
        List<Image> images = imageRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Image image : images) {
            Map<String, Object> imageData = new HashMap<>();
            imageData.put("id", image.getId());
            imageData.put("name", image.getName());
            imageData.put("type", image.getType());
            imageData.put("user_id", image.getUserId());  // ✅ Corrected to getUserId()
            response.add(imageData);
        }

        return ResponseEntity.ok(response);
    }
}
