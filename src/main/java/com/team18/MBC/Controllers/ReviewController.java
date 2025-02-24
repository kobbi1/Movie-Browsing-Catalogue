package com.team18.MBC.Controllers;

import com.team18.MBC.Services.MovieService;
import com.team18.MBC.Services.ReviewService;
import com.team18.MBC.core.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController  // Converts the controller into a REST API (returns JSON)
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final MovieService movieService;

    public ReviewController(ReviewService reviewService, MovieService movieService) {
        this.reviewService = reviewService;
        this.movieService = movieService;
    }

    // Get all reviews
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // Get a review by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewsById(id);
        return review != null ? ResponseEntity.ok(review) : ResponseEntity.notFound().build();
    }

    // Get reviews for a specific movie
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Review>> getReviewsForMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.getReviewsByMovieId(movieId));
    }

    // Get reviews for a specific TV show (same logic as movies)
    @GetMapping("/tvshow/{tvShowId}")
    public ResponseEntity<List<Review>> getReviewsForTvShow(@PathVariable Long tvShowId) {
        return ResponseEntity.ok(reviewService.getReviewsByMovieId(tvShowId));
    }

    // Create a new review
    @PostMapping("/create")
    public ResponseEntity<?> createReview(
            @RequestParam int rating,
            @RequestParam String reviewText,
            @RequestParam Long movieId,
            HttpSession session
    ) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(403).body(Map.of("error", "You must be logged in to submit a review."));
        }

        Movie movie = movieService.getMovieById(movieId);
        if (movie == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Movie not found."));
        }

        Review review = new Review(rating, reviewText, movie, loggedInUser);
        reviewService.saveReview(review);

        return ResponseEntity.ok(Map.of("message", "Review created successfully", "review", review));
    }

    // Delete a review
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        Review review = reviewService.getReviewsById(reviewId);

        if (review == null) {
            return ResponseEntity.notFound().build();
        }

        if (!review.getUser().equals(loggedInUser)) {
            return ResponseEntity.status(403).body(Map.of("error", "You are not authorized to delete this review."));
        }

        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
    }

    // Update a review
    @PutMapping("/update/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestParam int rating,
            @RequestParam String reviewText,
            HttpSession session
    ) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        Review review = reviewService.getReviewsById(reviewId);

        if (loggedInUser == null) {
            return ResponseEntity.status(403).body(Map.of("error", "You must be logged in to update a review."));
        }

        if (review == null || !review.getUser().equals(loggedInUser)) {
            return ResponseEntity.status(403).body(Map.of("error", "You are not authorized to update this review."));
        }

        review.setRating(rating);
        review.setReview_text(reviewText);
        reviewService.saveReview(review);

        return ResponseEntity.ok(Map.of("message", "Review updated successfully", "review", review));
    }
}
