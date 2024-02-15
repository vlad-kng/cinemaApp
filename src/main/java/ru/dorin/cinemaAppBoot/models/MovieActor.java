package ru.dorin.cinemaAppBoot.models;

import jakarta.persistence.*;
import org.springframework.core.SpringVersion;

@Entity
@Table(name = "movie_actor")
public class MovieActor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;
    @ManyToOne
    @JoinColumn(name = "actor_id")
    private Actor actor;
}
