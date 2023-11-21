package ru.dorin.cinemaAppBoot.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;


@Entity
@Table(name="director")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name")
    private String name;
    @OneToMany(mappedBy = "director", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private Set<Movie> movies;

    public Director(String name) {
        this.name = name;
    }

    public void addMovie(Movie movie){
        movies.add(movie);
    }
    public void removeMovie(Movie movie) {
        movies.remove(movie);
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


}
