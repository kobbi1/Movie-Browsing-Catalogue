package com.team18.MBC.Controllers;

import com.team18.MBC.Services.UserService;
import com.team18.MBC.core.Image;
import com.team18.MBC.core.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice  // Converts this into a REST API Middleware for all controllers
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute
    @ResponseBody
    public Map<String, Object> addGlobalAttributes(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (loggedInUser != null) {
            response.put("loggedInUser", Map.of(
                    "id", loggedInUser.getID(),
                    "username", loggedInUser.getUsername()
            ));
            response.put("isAuthenticated", true);

            // Fetch the profile image for the logged-in user
            Optional<Image> profileImage = userService.getProfileImageForUser(loggedInUser.getID());
            profileImage.ifPresent(image -> response.put("profileImage", Map.of(
                    "id", image.getId(),
                    "name", image.getName(),
                    "type", image.getType()
            )));
        } else {
            response.put("isAuthenticated", false);
        }

        return response;
    }
}
