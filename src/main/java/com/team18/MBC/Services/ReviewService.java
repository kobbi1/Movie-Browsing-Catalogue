package com.team18.MBC.Services;

import com.team18.MBC.Repositories.ReviewRepository;
import com.team18.MBC.core.Movie;
import com.team18.MBC.core.Review;
import com.team18.MBC.core.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ReviewService {

    private ReviewRepository reviewRepository;
    private MovieService movieService;
    private UserService userService;
    public ReviewService(ReviewRepository reviewRepository, MovieService movieService, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.movieService = movieService;
        this.userService = userService;
    }
    public List<Review> getAllReviews(){ return reviewRepository.findAll();}


    public Review getReviewsById(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        return review.orElse(null);
    }

    public List<Review> getReviewsByMovieId(Long movieId) {
        Movie movie = movieService.getMovieById(movieId);
        return reviewRepository.findByMovie(movie);
    }

    public double getAverageRatingForMovie(long movieId) {
        Movie movie = movieService.getMovieById(movieId);
        List<Review> reviews = reviewRepository.findByMovieId(movieId);
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public boolean deleteReviewById(Long reviewId) {
        try {
            reviewRepository.deleteById(reviewId);
            return true;  // Deletion successful
        } catch (Exception e) {
            return false;  // Deletion failed
        }
    }
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }



    public Optional<Review> getReviewByUserAndMovie( Long movieId, Long userId) {
        Movie movie = movieService.getMovieById(movieId);
        User user = userService.findUserById(userId);
        return reviewRepository.findByUserIdAndMovieId(movieId, userId);
    }
}
