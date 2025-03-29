package com.team18.MBC.Controllers;

import com.team18.MBC.core.Movie;
import com.team18.MBC.core.User;
import com.team18.MBC.core.Watchlist;
import com.team18.MBC.Services.WatchlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController  // Converts the controller into a REST API (returns JSON)
@RequestMapping("/watchlists")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    // Get all watchlists
    @GetMapping
    public ResponseEntity<List<Watchlist>> getAllWatchlists() {
        return ResponseEntity.ok(watchlistService.findAll());
    }

    // Get a watchlist by ID
    @GetMapping("/{watchlistId}")
    public ResponseEntity<?> getWatchlistById(@PathVariable Long watchlistId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        Optional<Watchlist> watchlistOpt = watchlistService.getWatchlistById(watchlistId);
        if (watchlistOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Watchlist watchlist = watchlistOpt.get();
        List<Movie> watchlistItems = watchlistService.getMoviesInWatchlist(watchlistId);
        boolean isOwnWatchlist = loggedInUser != null && Objects.equals(watchlist.getUser().getID(), loggedInUser.getID());

        Map<String, Object> response = new HashMap<>();
        response.put("watchlist", watchlist);
        response.put("watchlistItems", watchlistItems);
        response.put("isOwnWatchlist", isOwnWatchlist);

        return ResponseEntity.ok(response);
    }

    // Get watchlists for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserWatchlists(@PathVariable Long userId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        boolean isOwnProfile = loggedInUser != null && Objects.equals(loggedInUser.getID(), userId);

        List<Watchlist> userWatchlists = watchlistService.getWatchlistsByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("userWatchlists", userWatchlists);
        response.put("isOwnProfile", isOwnProfile);

        return ResponseEntity.ok(response);
    }

    // Create a new watchlist
    @PostMapping("/create")
    public ResponseEntity<?> createWatchlist(@RequestBody Watchlist watchlist, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (loggedInUser == null) {
            return ResponseEntity.status(403).body(Map.of("error", "You must be logged in to create a watchlist."));
        }

        watchlist.setUser(loggedInUser);
        watchlistService.saveWatchlist(watchlist);

        return ResponseEntity.ok(Map.of("message", "Watchlist created successfully", "watchlist", watchlist));
    }

    @DeleteMapping("/delete/{watchlistId}/user/{userId}")
    public ResponseEntity<?> deleteWatchlist(@PathVariable Long watchlistId, @PathVariable Long userId) {
        Watchlist watchlist = watchlistService.findById(watchlistId);

        if (watchlist == null) {
            return ResponseEntity.notFound().build();
        }

        if (!Objects.equals(watchlist.getUser().getID(), userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Not authorized to delete this watchlist"));
        }

        watchlistService.delete(watchlistId);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }


    // Remove a movie from a watchlist
    @DeleteMapping("/{watchlistId}/remove-movie/{movieId}")
    public ResponseEntity<?> removeMovieFromWatchlist(@PathVariable Long watchlistId, @PathVariable Long movieId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        Optional<Watchlist> watchlistOpt = watchlistService.getWatchlistById(watchlistId);
        if (watchlistOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Watchlist watchlist = watchlistOpt.get();
        if (!Objects.equals(watchlist.getUser().getID(), loggedInUser.getID())) {
            return ResponseEntity.status(403).body(Map.of("error", "You are not authorized to modify this watchlist."));
        }

        watchlistService.removeMovieFromWatchlist(watchlistId, movieId);
        return ResponseEntity.ok(Map.of("message", "Movie removed from watchlist"));
    }
}
