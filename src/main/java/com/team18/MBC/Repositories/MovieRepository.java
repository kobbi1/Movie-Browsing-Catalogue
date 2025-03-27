package com.team18.MBC.Repositories;

import com.team18.MBC.core.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByType(String type);

    Optional<Movie> findByIdAndType(Long id, String type);

    @Query("SELECT m FROM Movie m WHERE m.genre LIKE %:genre% AND m.type = 'tv_show'")
    List<Movie> findTVShowByGenreContaining(String genre);


    @Query("SELECT m FROM Movie m WHERE m.genre LIKE %:genre% AND m.type = 'movie'")
    List<Movie> findMovieByGenreContaining(String genre);

    @Query(value = "SELECT m.title, m.genre, m.director, m.releaseYear, m.description, m.cover_image, AVG(r.rating) as rating " +
            "FROM movies m JOIN reviews r ON m.id = r.movie_id " +
            "WHERE m.type = 'movie' " +
            "GROUP BY m.id, m.title, m.genre, m.director, m.releaseYear, m.description, m.cover_image " +
            "ORDER BY rating DESC " +
            "LIMIT 10", nativeQuery = true)
    List<Object[]> getTopMovies();


}
