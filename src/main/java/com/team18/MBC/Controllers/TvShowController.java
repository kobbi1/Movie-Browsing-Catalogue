package com.team18.MBC.Controllers;

import com.team18.MBC.Repositories.ReviewRepository;
import com.team18.MBC.Services.MovieService;
import com.team18.MBC.Services.ReviewService;
import com.team18.MBC.Services.WatchlistItemsService;
import com.team18.MBC.Services.WatchlistService;
import com.team18.MBC.core.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController  // Converts the controller into a REST API (returns JSON)
@RequestMapping("/tvshows")
public class TvShowController {

    private final WatchlistService watchlistService;
    private final MovieService movieService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final WatchlistItemsService watchlistItemsService;

    @Autowired
    public TvShowController(MovieService movieService, ReviewService reviewService, WatchlistService watchlistService, ReviewRepository reviewRepository, WatchlistItemsService watchlistItemsService) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.watchlistService = watchlistService;
        this.reviewRepository = reviewRepository;
        this.watchlistItemsService = watchlistItemsService;
    }

    // Get all TV shows
    @GetMapping
    public ResponseEntity<List<Movie>> getAllTvShows() {
        return ResponseEntity.ok(movieService.getAllTvShows());
    }

    // Get a specific TV show by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTvShowById(@PathVariable Long id, HttpSession session) {
        Movie tvShow = movieService.getTvShowById(id);
        if (tvShow == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("tvShow", tvShow);
        response.put("reviews", reviewRepository.findByMovieId(id));
        response.put("averageRating", reviewService.getAverageRatingForMovie(id));

        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser != null) {
            response.put("userWatchlists", watchlistService.getWatchlistsByUserId(loggedInUser.getID()));
            response.put("userHasReviewed", reviewRepository.findByMovieId(id).stream()
                    .anyMatch(review -> review.getUser().equals(loggedInUser)));
        } else {
            response.put("userHasReviewed", false);
        }

        response.put("actors", movieService.getActorsByMovieId(id));
        return ResponseEntity.ok(response);
    }

    // Get all unique TV show categories
    @GetMapping("/categories")
    public ResponseEntity<Set<String>> getTvShowCategories() {
        List<Movie> tvShows = movieService.getAllTvShows();
        Set<String> uniqueGenres = new HashSet<>();
        for (Movie movie : tvShows) {
            uniqueGenres.addAll(Arrays.asList(movie.getGenre().split(", ")));
        }
        return ResponseEntity.ok(uniqueGenres);
    }

    // Get TV shows by a specific category
    @GetMapping("/categories/{category}")
    public ResponseEntity<List<Movie>> getTvShowsBySpecificCategory(@PathVariable String category) {
        return ResponseEntity.ok(movieService.getTvShowsByGenre(category));
    }

    // Add a TV show to a user's watchlist
    @PostMapping("/add-to-watchlist")
    public ResponseEntity<?> addToWatchlist(@RequestParam Long movieId, @RequestParam Long watchlistId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(403).body(Map.of("error", "User not logged in"));
        }

        Optional<Movie> movie = Optional.ofNullable(movieService.getTvShowById(movieId));
        Optional<Watchlist> watchlist = Optional.ofNullable(watchlistService.findById(watchlistId));

        if (movie.isEmpty() || watchlist.isEmpty() || !Objects.equals(watchlist.get().getUser().getID(), loggedInUser.getID())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid watchlist or TV show"));
        }

        WatchlistItems watchlistItem = new WatchlistItems();
        watchlistItem.setMovie(movie.get());
        watchlistItem.setWatchlist(watchlist.get());
        watchlistItemsService.save(watchlistItem);

        return ResponseEntity.ok(Map.of("message", "TV show added to watchlist"));
    }
}
