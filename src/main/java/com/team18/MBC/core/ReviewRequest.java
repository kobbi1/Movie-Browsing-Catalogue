package com.team18.MBC.core;



public class ReviewRequest {
    private int rating;
    private String reviewText;
    private Long movieId;
    private Long userId;

    public ReviewRequest() {
        // Default constructor for JSON deserialization
    }

    public ReviewRequest(int rating, String reviewText, Long movieId, Long userId) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.movieId = movieId;
        this.userId = userId;
    }

    // Getters and Setters
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
