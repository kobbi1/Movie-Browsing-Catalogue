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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;  // Service to fetch user details.

    // GET method to render the upload form
    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "uploadForm";  // Points to a Thymeleaf template named "uploadForm.html"
    }

    // POST method to handle the image upload
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
        // Get the currently authenticated user

        User user = (User) session.getAttribute("LoggedInUser");
        // Fetch the User entity by username


        if (user != null) {
            try {
                // Save the image and associate it with the user
                imageService.saveImage(file, user);
                model.addAttribute("message", "Profile picture uploaded successfully.");
            } catch (IOException e) {
                model.addAttribute("message", "Error uploading image: " + e.getMessage());
            }
        } else {
            model.addAttribute("message", "User not found.");
        }

        return "uploadForm";  // Return to the same form after upload
    }


    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(image -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/jpeg") // Change according to your image type
                        .body(image.getData()))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/")
    public String listImages(Model model) {
        List<Image> images = imageRepository.findAll();
        model.addAttribute("images", images);
        return "images"; // This should match your Thymeleaf template name
    }

}
