package ru.dorin.cinemaAppBoot.models;


import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;


@Entity
@Table(name="director")
public class Director {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name")
    private String name;
    @OneToMany(mappedBy = "director", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private Set<Movie> movies;

    public Director(){}

    public Director(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "Director{" +
                "id=" + id +
                ", name='" + name +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Director director)) return false;
        return name.equals(director.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void removeMovie(Movie movie) {
        movies.remove(movie);
    }
}
