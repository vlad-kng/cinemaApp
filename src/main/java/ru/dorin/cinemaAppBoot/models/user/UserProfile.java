package ru.dorin.cinemaAppBoot.models.user;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import ru.dorin.cinemaAppBoot.models.Actor;
import ru.dorin.cinemaAppBoot.models.Movie;

import java.util.Set;

@Entity
@Table(name = "user_profile")
@NoArgsConstructor
@Getter
@Setter
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @NotNull
    @Column(name="username")
    private String username;
    @NotNull
    @Column(name = "password")
    private String password;
    @NotNull
    @Size(min = 2, max = 100, message = "Name should be between 2 and 30 char")
    @Column(name="name")
    private String name;
    @NotNull
    @Column(name="year_of_birth")
    private int yearOfBirth;

    @Column(name="email")
    private String email;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name="user_movie_watched",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="movie_id"))
    private Set<Movie> moviesWatched;
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name="user_movie_not_interested",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="movie_id"))
    private Set<Movie> notInterestedMovies;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name="user_actor",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="actor_id"))
    private Set<Actor> favoriteActors;
    @ManyToMany()
    @JoinTable(
            name="user_movie_liked",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="movie_id"))
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST,
            org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.REFRESH,
            org.hibernate.annotations.CascadeType.DETACH})
    private Set<Movie> moviesLiked;


    public UserProfile(String username, String password, String name, int yearOfBirth, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.email = email;
        this.role = Role.ROLE_USER;
    }

    public void addLikedMovie(Movie movie){
        moviesLiked.add(movie);
    }
    public void removeLikedMovie(Movie movie){
        moviesLiked.remove(movie);
    }

    public boolean isMovieLiked(Movie movie){
        return moviesLiked.contains(movie);
    }

    public boolean isMovieDisliked(Movie movie){
        return notInterestedMovies.contains(movie);
    }
    public void addMovieToDislike(Movie movie)
    {
        if(movie.isMovieLikedByUser(this)){
            movie.like(this);
        }
        notInterestedMovies.add(movie);
    }
    public void removeMovieFromDislike(Movie movie){
        notInterestedMovies.remove(movie);
    }

    public void dislike(Movie movie){
        if(!isMovieDisliked(movie)){
            addMovieToDislike(movie);
        }else removeMovieFromDislike(movie);
    }

    public boolean isMovieWatched(Movie movie){
        return moviesWatched.contains(movie);
    }

    public void addWatchedMovie(Movie movie){
        moviesWatched.add(movie);
    }
    public void removeWatchedMovie(Movie movie){
        moviesWatched.remove(movie);
    }
}
