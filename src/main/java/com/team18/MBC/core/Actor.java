package com.team18.MBC.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "actors")
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
    private String gender;
    private String Image;


    @ManyToMany
    @JoinTable(
            name = "movie_actor", // Join table name
            joinColumns = @JoinColumn(name = "actor_id"), // Foreign key for actor
            inverseJoinColumns = @JoinColumn(name = "movie_id") // Foreign key for movie
    )
    @JsonBackReference
    private List<Movie> movies; // List of movies the actor has appeared in

    public Actor() {
    }

    public Actor(String name, int age, String gender, String Image) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.Image = Image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }
}
