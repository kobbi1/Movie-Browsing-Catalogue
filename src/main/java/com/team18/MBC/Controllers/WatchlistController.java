package com.team18.MBC.Controllers;

import com.team18.MBC.core.Movie;
import com.team18.MBC.core.User;
import com.team18.MBC.core.Watchlist;
import com.team18.MBC.Services.WatchlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;
    private MovieController movieService;

    @GetMapping("/watchlists")
    public String getAllWatchlists(Model model) {
        List<Watchlist> watchlists = watchlistService.findAll();
        model.addAttribute("watchlists", watchlists);
        return "watchlists";
    }

    @GetMapping("/watchlists/{watchlistId}")
    public String getWatchlistItems(@PathVariable Long watchlistId, Model model, HttpSession session) {
        // Retrieve the logged-in user from the session
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        // Retrieve the watchlist by its ID
        Optional<Watchlist> watchlistOpt = watchlistService.getWatchlistById(watchlistId);

        // If the watchlist exists, proceed with logic
        if (watchlistOpt.isPresent()) {
            Watchlist watchlist = watchlistOpt.get();

            // Retrieve the items in the watchlist
            List<Movie> watchlistItems = watchlistService.getMoviesInWatchlist(watchlistId);

            // Add the watchlist items and watchlist to the model
            model.addAttribute("watchlistItems", watchlistItems);
            model.addAttribute("watchlist", watchlist);

            // Check if the logged-in user owns the watchlist
            boolean isOwnWatchlist = loggedInUser != null && loggedInUser.getID() == watchlist.getUser().getID();
            model.addAttribute("isOwnWatchlist", isOwnWatchlist);

            // Return the view name for watchlist details
            return "watchlist-details";
        }

        // If the watchlist does not exist, return an error or redirect
        return "redirect:/error";  // Or another appropriate response
    }

    @GetMapping("/users/{userId}/watchlists")
    public String getUserWatchlists(@PathVariable Long userId, Model model, HttpSession session) {

        User loggedInUser = (User) session.getAttribute("LoggedInUser");


        boolean isOwnProfile = loggedInUser != null && loggedInUser.getID() == userId;
        model.addAttribute("isOwnProfile", isOwnProfile);

        List<Watchlist> userWatchlists = watchlistService.getWatchlistsByUserId(userId);
        model.addAttribute("userWatchlists", userWatchlists);
        // Add an empty Watchlist object for the form
        model.addAttribute("watchlist", new Watchlist());
        return "userWatchlists";
    }

    @PostMapping("/watchlists/create")
    public String createWatchlist(@ModelAttribute Watchlist watchlist, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (loggedInUser != null) {
            watchlist.setUser(loggedInUser); // Associate the logged-in user with the new watchlist
            watchlistService.saveWatchlist(watchlist); // Save the watchlist
        }

        // Redirect back to the user's watchlists page
        return "redirect:/users/" + loggedInUser.getID() + "/watchlists";
    }

    @PostMapping("/watchlists/delete/{watchlistId}")
    public String deleteWatchlist(@PathVariable Long watchlistId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        // Find the watchlist
        Watchlist watchlist = watchlistService.findById(watchlistId);

        // Check if the watchlist exists and if the logged-in user is the owner
        if (watchlist != null && watchlist.getUser().getID() == loggedInUser.getID()) {
            watchlistService.delete(watchlistId); // Call service to delete the watchlist
            return "redirect:/users/" + loggedInUser.getID() + "/watchlists"; // Redirect to the user's watchlist page
        }

        // If the watchlist does not belong to the logged-in user, redirect back with an error
        return "redirect:/error";
    }


    @PostMapping("/watchlists/{watchlistId}/remove-movie/{movieId}")
    public String removeMovieFromWatchlist(@PathVariable Long watchlistId, @PathVariable Long movieId, HttpSession session) {
        // Fetch the logged-in user
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        // Ensure the user is authorized to modify the watchlist
        Optional<Watchlist> watchlist = watchlistService.getWatchlistById(watchlistId);
        if (watchlist != null && loggedInUser != null && (watchlist.get().getUser().getID() == (loggedInUser.getID()))) {
            // Remove the movie from the watchlist
            watchlistService.removeMovieFromWatchlist(watchlistId, movieId);
            return "redirect:/watchlists/" + watchlistId; // Redirect back to the watchlist page
        }

        // If the watchlist doesn't exist or the user is unauthorized, redirect to an error page
        return "redirect:/error";
    }


}
