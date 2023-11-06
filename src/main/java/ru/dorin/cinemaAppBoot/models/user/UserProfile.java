package ru.dorin.cinemaAppBoot.models.user;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.dorin.cinemaAppBoot.models.Actor;
import ru.dorin.cinemaAppBoot.models.Movie;

import java.util.Set;

@Entity
@Table(name = "user_profile")
@NoArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter
    @Setter
    private int id;
    @NotNull
    @Column(name="username")
    @Getter
    @Setter
    private String username;
    @NotNull
    @Getter
    @Setter
    @Column(name = "password")
    private String password;
    @NotNull
    @Getter
    @Setter
    @Size(min = 2, max = 100, message = "Name should be between 2 and 30 char")
    @Column(name="name")
    private String name;
    @NotNull
    @Getter
    @Setter
    @Column(name="year_of_birth")
    private int yearOfBirth;

    @Getter
    @Setter
    @Column(name="email")
    private String email;
    @Column(name = "role")
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name="user_movie_watched",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="movie_id"))
    private Set<Movie> moviesWatched;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name="user_movie_not_interested",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="movie_id"))
    private Set<Movie> notInterestedMovies;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name="user_actor",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="actor_id"))
    private Set<Actor> favoriteActors;

//    private Set<Movie> moviesLike; //допилить


    public UserProfile(String username, String password, String name, int yearOfBirth, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.email = email;
        this.role = Role.ROLE_USER;
    }
}
