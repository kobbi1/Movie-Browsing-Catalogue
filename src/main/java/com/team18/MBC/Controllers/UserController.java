package com.team18.MBC.Controllers;

import com.team18.MBC.Services.ImageService;
import com.team18.MBC.Services.UserService;
import com.team18.MBC.core.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    UserService userService;
    ImageService imageService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Existing Endpoints

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupGET(User user) {
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupPOST(User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "redirect:/signup";
        }
        User exists = userService.findByUsername(user.getUsername());
        if (exists == null) {
            userService.save(user);
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGET(User user) {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPOST(User user, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "login";
        }
        User exists = userService.login(user);
        if (exists != null) {
            session.setAttribute("LoggedInUser", exists);
            model.addAttribute("LoggedInUser", exists);
            return "redirect:/users/profile";
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/loggedin", method = RequestMethod.GET)
    public String loggedinGET(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            model.addAttribute("LoggedInUser", sessionUser);
            return "loggedInUser";
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/delete/{username}", method = RequestMethod.GET)
    public String deleteUser(@PathVariable("username") String username, Model model, HttpSession session) {
        User userToDelete = userService.findByUsername(username);
        userService.delete(userToDelete);
        session.invalidate(); // Manually invalidate the session
        return "redirect:/";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getAllUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserAPIById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);

        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @RequestMapping(value = "/users/{id}/profile", method = RequestMethod.GET)
    public String getUserById(@PathVariable("id") Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        User profileUser = userService.findUserById(id);
        if (profileUser != null) {
            System.out.println("LoggedInUser ID: " + (loggedInUser != null ? loggedInUser.getID() : "null"));
            System.out.println("ProfileUser ID: " + profileUser.getID());
            model.addAttribute("user", profileUser);
            boolean isOwnProfile = loggedInUser != null && loggedInUser.getID() == profileUser.getID();
            model.addAttribute("isOwnProfile", isOwnProfile);

            Optional<Image> profileImage = userService.getProfileImageForUser(profileUser.getID());
            profileImage.ifPresent(image -> model.addAttribute("profileImage", image));


            return "userProfile";
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/users/{ID}/update-password", method = RequestMethod.PATCH)
    public String updatePassword(
            @PathVariable("ID") Long ID,
            @ModelAttribute("passwordChangeRequest") PasswordChangeRequest passwordChangeRequest,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "updatePassword";
        }

        User user = userService.findUserById(ID);
        if (user != null) {
            userService.updatePassword(user, passwordChangeRequest.getNewPassword());
            return "redirect:/users/profile";
        }

        return "redirect:/";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Perform any custom logic here
        session.invalidate(); // Manually invalidate the session
        return "redirect:/"; // Redirect to the home page or another URL
    }


    @RequestMapping(value = "/users/{ID}/update-password", method = RequestMethod.GET)
    public String showUpdatePasswordPage(@PathVariable("ID") Long ID, Model model) {
        User user = userService.findUserById(ID);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
            return "updatePassword";
        }
        return "redirect:/";
    }


    @RequestMapping(value = "/users/profile", method = RequestMethod.GET)
    public String getLoggedInUserProfile(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            model.addAttribute("user", sessionUser);
            model.addAttribute("isOwnProfile", true);

            Optional<Image> profileImage = userService.getProfileImageForUser(sessionUser.getID());
            profileImage.ifPresent(image -> model.addAttribute("profileImage", image));

            return "userProfile";
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/user-profile/settings", method = RequestMethod.GET)
    public String getUserSettings(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if (sessionUser != null) {
            model.addAttribute("user", sessionUser);
            return "usersettings";
        }
        return "redirect:/login";
    }


}
