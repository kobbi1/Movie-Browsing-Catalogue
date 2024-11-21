package com.team18.MBC.Controllers;

import com.team18.MBC.Repositories.ReviewRepository;
import com.team18.MBC.Services.MovieService;
import com.team18.MBC.Services.ReviewService;
import com.team18.MBC.Services.WatchlistItemsService;
import com.team18.MBC.Services.WatchlistService;
import com.team18.MBC.core.*;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private WatchlistService watchlistService;
    private MovieService movieService;
    private ReviewService reviewService;
    private ReviewRepository reviewRepository;
     @Autowired
    private WatchlistItemsService watchlistItemsService;


    public MovieController(MovieService movieService, ReviewService reviewService, WatchlistService watchlistService, ReviewRepository reviewRepository) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.watchlistService = watchlistService;
        this.reviewRepository = reviewRepository;

    }

    @GetMapping
    public String getAllMovies(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        model.addAttribute("contextPath", "movies");
        model.addAttribute("contentTitle", "Movies");


        return "movies";
    }

    @GetMapping("/{id}")
    public String getMovieById(@PathVariable Long id, Model model, HttpSession session) {
        Movie movie = movieService.getMovieById(id);
        if (movie != null) {
            model.addAttribute("movie", movie);
            model.addAttribute("contextPath", "movies");

            List<Review> reviews = reviewRepository.findByMovieId(id);
            double averageRating = reviewService.getAverageRatingForMovie(id);
            model.addAttribute("reviews", reviews);
            model.addAttribute("averageRating", averageRating);

            // Fetch the logged-in user from the session
            User loggedInUser = (User) session.getAttribute("LoggedInUser");
            boolean userHasReviewed = false;
            if (loggedInUser != null) {
                userHasReviewed = reviews.stream()
                        .anyMatch(review -> review.getUser().equals(loggedInUser));
            }
            model.addAttribute("userHasReviewed", userHasReviewed);

            if (loggedInUser != null) {
                // Fetch the watchlists for the logged-in user
                List<Watchlist> userWatchlists = watchlistService.getWatchlistsByUserId(loggedInUser.getID());
                model.addAttribute("userWatchlists", userWatchlists);
            }

            List<Actor> actors = movieService.getActorsByMovieId(id);
            model.addAttribute("actors", actors);

            return "movie-details";
        } else {
            return "404";
        }
    }


    @GetMapping("/categories")
    public String getMovieCategories(Model model) {
        List<Movie> movies = movieService.getAllMovies();

        Set<String> uniqueGenres = new HashSet<>();
        for (Movie movie : movies) {
            String[] genres = movie.getGenre().split(", ");
            uniqueGenres.addAll(Arrays.asList(genres));
        }

        // Add the unique genres to the model
        model.addAttribute("categories", uniqueGenres);
        return "movieCategories";
    }

    @GetMapping("/categories/{category}")
    public String getMoviesBySpecificCategory(@PathVariable String category, Model model) {
        List<Movie> filteredMovies = movieService.getMoviesByGenre(category);
        model.addAttribute("movies", filteredMovies);
        return "movieCategoriesSpecific";
    }

    @GetMapping("/top-movies")
    public String getTopMovies(Model model) {
        List<Movie.MovieRating> topMovies = movieService.getTopMovies();
        model.addAttribute("movies", topMovies);
        return "topMovies";
    }

    @PostMapping("/add-to-watchlist")
    public String addToWatchlist(@RequestParam Long movieId, @RequestParam Long watchlistId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");

        if (loggedInUser != null) {
            Optional<Movie> movie = Optional.ofNullable(movieService.getMovieById(movieId));
            Optional<Watchlist> watchlist = Optional.ofNullable(watchlistService.findById(watchlistId));

            if (movie.isPresent() && watchlist.isPresent() && watchlist.get().getUser().getID() == loggedInUser.getID()) {
                // Create a new watchlist_item entry
                WatchlistItems watchlistItem = new WatchlistItems();
                watchlistItem.setMovie(movie.get());
                watchlistItem.setWatchlist(watchlist.get());

                // Save the new entry to the watchlist_items table
                watchlistItemsService.save(watchlistItem);

                return "redirect:/movies/" + movieId; // Redirect to movie details page
            }
        }

        return "redirect:/error"; // Redirect to an error page if something goes wrong
    }
}
