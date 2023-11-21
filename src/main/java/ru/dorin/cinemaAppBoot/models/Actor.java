package ru.dorin.cinemaAppBoot.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="actor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Actor {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name")
    private String name;

    @ManyToMany()
    @JoinTable(
            name="movie_actor",
            joinColumns = @JoinColumn(name="actor_id"),
            inverseJoinColumns = @JoinColumn(name="movie_id"))
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST,
            org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.REFRESH,
            org.hibernate.annotations.CascadeType.DETACH})
    private Set<Movie> movies;


    public Actor(String name) {
        this.name = name;
    }

    public void addMovie(Movie movie){
        if(this.movies==null){
            movies=new HashSet<>();
        }
        movies.add(movie);
    }
    public void removeMovie(Movie movie) {
        this.movies.remove(movie);
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", name='" + name +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Actor actor)) return false;
        return name.equals(actor.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

