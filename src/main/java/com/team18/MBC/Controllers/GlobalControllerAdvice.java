package com.team18.MBC.Controllers;

import com.team18.MBC.Services.UserService;
import com.team18.MBC.core.Image;
import com.team18.MBC.core.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;


@Component
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    // This method runs for all controllers and adds attributes to the model
    @ModelAttribute
    public void addGlobalAttributes(HttpSession session, Model model) {
        // Retrieve the logged-in user from the session
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        // Add the loggedInUser to the model if it exists
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("isAuthenticated", true);

            // Fetch the profile image for the logged-in user
            Optional<Image> profileImage = userService.getProfileImageForUser(loggedInUser.getID());
            if (profileImage.isPresent()) {
                profileImage.ifPresent(image -> model.addAttribute("profileImage", image));
            }
        } else {
            model.addAttribute("isAuthenticated", false);
        }
    }
}
