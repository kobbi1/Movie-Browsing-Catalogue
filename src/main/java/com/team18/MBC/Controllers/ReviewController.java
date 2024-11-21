package com.team18.MBC.Controllers;

import com.team18.MBC.Services.MovieService;
import com.team18.MBC.Services.ReviewService;
import com.team18.MBC.core.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private ReviewService reviewService;
    private MovieService movieService;

    public ReviewController(ReviewService reviewService, MovieService movieService) {
        this.reviewService = reviewService;
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews); // 200 OK
    }
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewsById(id);
        if (review != null) {
            return ResponseEntity.ok(review);

        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
    @GetMapping("/movie/{movieId}")
    public String getReviewsForMovie(@PathVariable Long movieId, Model model) {
        List<Review> reviews = reviewService.getReviewsByMovieId(movieId);
        model.addAttribute("reviews", reviews);
        return "movie-details";
    }

    @GetMapping("/tvshow/{tvShowId}")
    public String getReviewsForTvShow(@PathVariable Long tvShowId, Model model) {
        List<Review> reviews = reviewService.getReviewsByMovieId(tvShowId);
        model.addAttribute("reviews", reviews);
        return "movie-details";
    }
    @PostMapping("/create")
    public String createReview(
            @RequestParam int rating,
            @RequestParam String reviewText,
            @RequestParam Long movieId,
            @RequestParam String contextPath,
            HttpSession session,
            Model model
    ) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser == null) {
            model.addAttribute("error", "You must be logged in to submit a review.");
            return "redirect:/login";
        }

        Movie movie = null;
        if (contextPath.equals("movies")) {
            movie = movieService.getMovieById(movieId);
        } else if (contextPath.equals("tvshows")) {
            movie = movieService.getTvShowById(movieId);
        }

        if (movie == null) {
            model.addAttribute("error", "Content not found.");
            return "redirect:/" + contextPath;
        }

        Review review = new Review(rating, reviewText, movie, loggedInUser);
        reviewService.saveReview(review);

        return "redirect:/" + contextPath + "/" + movieId;
    }
    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        Review review = reviewService.getReviewsById(reviewId);
        if (review != null && review.getUser().equals(loggedInUser)) {
            reviewService.deleteReview(reviewId);

            String contextPath = review.getMovie().getClass().getSimpleName().toLowerCase();
            return "redirect:/" + contextPath + "/" + review.getMovie().getId();
        } else {
            return "redirect:/error";
        }
    }

    @RequestMapping(value = "/delete/{reviewId}/{contextPath}/{movieId}", method = RequestMethod.GET)
    public String deleteUser(@PathVariable("reviewId") Long reviewId, @PathVariable("contextPath") String contextPath,@PathVariable("movieId") Long movieId, Model model) {
        reviewService.deleteReview(reviewId);
        Review review = reviewService.getReviewsById(reviewId);
        System.out.println("User has reviewed: " + contextPath);
        return "redirect:/" + contextPath + "/" + movieId;
    }

    @PostMapping("/update/{reviewId}")
    public String updateReview(
            @PathVariable Long reviewId,
            @RequestParam int rating,
            @RequestParam String reviewText,
            @RequestParam String contextPath,
            HttpSession session,
            Model model
    ) {
        User loggedInUser = (User) session.getAttribute("LoggedInUser");
        if (loggedInUser == null) {
            model.addAttribute("error", "You must be logged in to update a review.");
            return "redirect:/login";
        }

        Review review = reviewService.getReviewsById(reviewId);
        if (review == null || !review.getUser().equals(loggedInUser)) {
            model.addAttribute("error", "You are not authorized to update this review.");
            return "redirect:/" + contextPath;
        }

        review.setRating(rating);
        review.setReview_text(reviewText);

        reviewService.saveReview(review);

        return "redirect:/" + contextPath + "/" + review.getMovie().getId();
    }
}
