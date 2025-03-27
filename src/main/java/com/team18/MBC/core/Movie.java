package com.team18.MBC.core;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {

    @OneToMany(mappedBy = "movie")
    private List<WatchlistItems> watchlistItems;

    @ManyToMany
    @JoinTable(
            name = "movie_actor", // join table name
            joinColumns = @JoinColumn(name = "movie_id"), // foreign key for movie
            inverseJoinColumns = @JoinColumn(name = "actor_id") // foreign key for actor
    )
    @JsonManagedReference
    private List<Actor> cast;  // List of actors (cast)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String genre;
    private String director;

    @Column(name = "RELEASEYEAR")
    private int releaseYear;

    private String description;

    private String type;

    private String coverImage;
    public Movie() {
    }
    public Movie(String title, String genre, String director, int releaseYear, String description, String type, String coverImage) {
        this.title = title;
        this.genre = genre;
        this.director = director;
        this.releaseYear = releaseYear;
        this.description = description;
        this.type = type;
        this.coverImage = coverImage;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<Actor> getCast() {
        return cast;
    }

    public void setCast(List<Actor> cast) {
        this.cast = cast;
    }

    public static class MovieRating {
        private String title;
        private String genre;
        private String director;
        private int releaseYear;
        private String description;
        private String coverImage;
        private double rating;

        public MovieRating(String title, String genre, String director, int releaseYear, String description, String coverImage, double rating) {
            this.title = title;
            this.genre = genre;
            this.director = director;
            this.releaseYear = releaseYear;
            this.description = description;
            this.coverImage = coverImage;
            this.rating = rating;
        }

        // Getters and setters for MovieRating
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getDirector() {
            return director;
        }

        public void setDirector(String director) {
            this.director = director;
        }

        public int getReleaseYear() {
            return releaseYear;
        }

        public void setReleaseYear(int releaseYear) {
            this.releaseYear = releaseYear;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCoverImage() { return coverImage; }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }
    }
}
