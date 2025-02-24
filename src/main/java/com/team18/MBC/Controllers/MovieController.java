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

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final WatchlistService watchlistService;
    private final MovieService movieService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final WatchlistItemsService watchlistItemsService;

    @Autowired
    public MovieController(MovieService movieService, ReviewService reviewService, WatchlistService watchlistService, ReviewRepository reviewRepository, WatchlistItemsService watchlistItemsService) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.watchlistService = watchlistService;
        this.reviewRepository = reviewRepository;
        this.watchlistItemsService = watchlistItemsService;
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        System.out.println(movies);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id, HttpSession session) {
        Movie movie = movieService.getMovieById(id);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("movie", movie);
        response.put("reviews", reviewRepository.findByMovieId(id));
        response.put("averageRating", reviewService.getAverageRatingForMovie(id));

        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser != null) {
            List<Watchlist> userWatchlists = watchlistService.getWatchlistsByUserId(loggedInUser.getID());
            response.put("userWatchlists", userWatchlists);
            response.put("userHasReviewed", reviewRepository.findByMovieId(id).stream()
                    .anyMatch(review -> review.getUser().equals(loggedInUser)));
        } else {
            response.put("userHasReviewed", false);
        }

        response.put("actors", movieService.getActorsByMovieId(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<Set<String>> getMovieCategories() {
        List<Movie> movies = movieService.getAllMovies();
        Set<String> uniqueGenres = new HashSet<>();
        for (Movie movie : movies) {
            uniqueGenres.addAll(Arrays.asList(movie.getGenre().split(", ")));
        }
        return ResponseEntity.ok(uniqueGenres);
    }

    @GetMapping("/categories/{category}")
    public ResponseEntity<List<Movie>> getMoviesBySpecificCategory(@PathVariable String category) {
        List<Movie> filteredMovies = movieService.getMoviesByGenre(category);
        return ResponseEntity.ok(filteredMovies);
    }

    @GetMapping("/top-movies")
    public ResponseEntity<List<Movie.MovieRating>> getTopMovies() {
        return ResponseEntity.ok(movieService.getTopMovies());
    }

    @PostMapping("/add-to-watchlist")
    public ResponseEntity<String> addToWatchlist(@RequestParam Long movieId, @RequestParam Long watchlistId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(403).body("User not logged in");
        }

        Optional<Movie> movie = Optional.ofNullable(movieService.getMovieById(movieId));
        Optional<Watchlist> watchlist = Optional.ofNullable(watchlistService.findById(watchlistId));

        if (movie.isEmpty() || watchlist.isEmpty() || !Objects.equals(watchlist.get().getUser().getID(), loggedInUser.getID())) {
            return ResponseEntity.badRequest().body("Invalid watchlist or movie");
        }

        WatchlistItems watchlistItem = new WatchlistItems();
        watchlistItem.setMovie(movie.get());
        watchlistItem.setWatchlist(watchlist.get());
        watchlistItemsService.save(watchlistItem);

        return ResponseEntity.ok("Movie added to watchlist");
    }
}
