package com.team18.MBC.Services;

import com.team18.MBC.Repositories.ActorRepository;
import com.team18.MBC.Repositories.MovieActorRepository;
import com.team18.MBC.Repositories.MovieRepository;
import com.team18.MBC.core.Actor;
import com.team18.MBC.core.Movie;
import com.team18.MBC.core.MovieActor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private MovieRepository movieRepository;
    private MovieActorRepository movieActorRepository;
    private ActorRepository actorRepository;

    public MovieService(MovieRepository movieRepository, MovieActorRepository movieActorRepository, ActorRepository actorRepository) {
        this.movieRepository = movieRepository;
        this.movieActorRepository = movieActorRepository;
        this.actorRepository = actorRepository;
    }

    public Movie getMovieById(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        return movie.orElse(null);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findByType("movie");
    }

    //Seperation for TV Shows instead of creating a new TV Show classes.

    public Movie getTvShowById(Long id) {
        Optional<Movie> movie = movieRepository.findByIdAndType(id, "tv_show");
        return movie.orElse(null);
    }

    public List<Movie> getAllTvShows() {
        return movieRepository.findByType("tv_show");
    }


    public List<Movie> getTvShowsByGenre(String genre) {
        return movieRepository.findTVShowByGenreContaining(genre);
    }

    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findMovieByGenreContaining(genre);
    }

    public List<Movie.MovieRating> getTopMovies() {
        List<Object[]> results = movieRepository.getTopMovies();
        List<Movie.MovieRating> topMovies = new ArrayList<>();

        for (Object[] row : results) {
            Movie.MovieRating movieRating = new Movie.MovieRating(
                    (String) row[0],
                    (String) row[1],
                    (String) row[2],
                    (Integer) row[3],
                    (String) row[4],
                    row[5] != null ? ((BigDecimal) row[5]).doubleValue() : 0.0  // Fix conversion here
            );
            topMovies.add(movieRating);
        }
        return topMovies;
    }
    public List<Actor> getActorsByMovieId(Long movieId) {
        List<MovieActor> movieActors = movieActorRepository.findByMovieId(movieId);
        List<Actor> actors = new ArrayList<>();
        for (MovieActor movieActor : movieActors) {
            Actor actor = movieActor.getActor();
            if (actor != null) {
                actors.add(actor);
            }
        }
        return actors;
    }
}
