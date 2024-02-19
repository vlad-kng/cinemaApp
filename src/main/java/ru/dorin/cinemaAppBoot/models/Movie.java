package ru.dorin.cinemaAppBoot.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.springframework.format.annotation.NumberFormat;
import ru.dorin.cinemaAppBoot.models.user.UserProfile;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull(message = "Name should not be empty")
    @Size(min = 2, max = 100, message = "Name should be between 2 and 100 char")
    @Column(name = "name")
    private String name;
    @Min(value = 1865, message = "The first film was made in 1865")
    @Column(name = "year_of_production")
    private int yearOfProduction;
    @Max(value = 10, message = "The max rate is 10.0")
    @NumberFormat(pattern = "#0.0")
    @Column(name = "rate")
    private double rate;
    @NotNull(message = "Info cannot be empty")
    @Column(name = "info")
    private String info;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "director_id", referencedColumnName = "id")
    private Director director;

    @Transient
    private String directorName;

    //    @NotEmpty(message = "a movie must have some actors")
    @ManyToMany()
    @JoinTable(
            name="movie_actor",
            joinColumns = @JoinColumn(name="movie_id"),
            inverseJoinColumns = @JoinColumn(name="actor_id"))
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST,
            org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.REFRESH
           })
    private Set<Actor> actors;
    @Transient
    private String[] actorsName;

    @Column(name = "movie_genre")
        private String genre;

    @ManyToMany(mappedBy = "moviesLiked")
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST,
            org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.REFRESH
            })
    private Set<UserProfile> usersWhoLiked;
    @NotNull(message = "the movie should have a poster")
    @Column(name = "poster")
    private String poster;

    @Column(name= "like_count")
    private int likeCount;

    public Movie(String name, int yearOfProduction, double rate, String info, Director director, String genre, String poster) {
        this.name = name;
        this.yearOfProduction = yearOfProduction;
        this.rate = rate;
        this.info = info;
        this.director = director;
        this.genre = genre;
        this.poster = poster;
        this.usersWhoLiked=new HashSet<>();
    }
    public Movie(String name, int yearOfProduction, double rate, String info, String genre, String directorName, String actorName, String poster) {
        this.name = name;
        this.yearOfProduction = yearOfProduction;
        this.rate = rate;
        this.info = info;
        this.genre = genre;
        this.director = new Director(directorName);
        this.actors = new HashSet<>(Collections.singletonList(new Actor(actorName)));
        this.poster=poster;
        this.usersWhoLiked = new HashSet<>();
    }
    public Movie(Movie movie) {
        this.id = movie.id;
        this.name = movie.name;
        this.yearOfProduction = movie.yearOfProduction;
        this.rate = movie.rate;
        this.info = movie.info;
        this.genre = movie.genre;
        this.poster=movie.poster;
        this.usersWhoLiked = new HashSet<>();
    }

    public Director getDirector() {
        if (director==null){return null;}
        return director;
    }

    public String getDirectorName() {
        return director==null?directorName:director.getName();
    }

    public int getDirectorId() {
        return director.getId();
    }

    public Set<Actor> getActors() {
        if(actors== null){return null;}
        return actors;
    }

    public void addActor(Actor actor) {
        if (this.actors == null) this.actors = new HashSet<>();
//        if (this.actors.contains(actor)) {actors.remove(actor);}
        this.actors.add(actor);
    }
    public Movie removeActors(){
        actors.forEach(actor -> {actors.remove(actor);
        actor.removeMovie(this);});
        return this;
    }

    public void removeActor(Actor actor) {
        this.actors.remove(actor);
    }

    public void setActors(Set<Actor> actors) {
        if (this.actors == null)
            this.actors = actors;
        else {
            for (Actor actor : actors) {
                this.actors.add(actor);
            }
        }
    }
    public void deleteActors() {
        this.actors = null;    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String[] getActorsName() {
        return actorsName;
    }

    public void setActorsName(String[] actorsName) {
        this.actorsName = actorsName;
    }


    @Override
    public String toString() {
        return "Movie{" +
                "name='" + name + '\'' +
                ", yearOfProduction=" + yearOfProduction +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;
        return id == movie.id && yearOfProduction == movie.yearOfProduction && Double.compare(movie.rate, rate) == 0 && name.equals(movie.name) && info.equals(movie.info) && director.equals(movie.director);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, yearOfProduction, rate, info, director);
    }

    public void setUsersWhoLiked(Set<UserProfile> usersWhoLiked) {
        this.usersWhoLiked = usersWhoLiked;
        likeCount=usersWhoLiked.size();
    }

    public void addUserWhoLiked(UserProfile user){
        usersWhoLiked.add(user);
        likeCount++;
    }
    public void removeUserWhoLiked(UserProfile user){
        usersWhoLiked.remove(user);
        likeCount--;
    }
    public boolean isMovieLikedByUser(UserProfile user){
        return usersWhoLiked.contains(user);
    }
    public int getLikeCount(){
        return likeCount;
    }

    public void like(UserProfile user){
        if(user.isMovieDisliked(this)){
            user.removeMovieFromDislike(this);
        }
        if(!isMovieLikedByUser(user)){
            addUserWhoLiked(user);
            user.addLikedMovie(this);
        }else{
            removeUserWhoLiked(user);
            user.removeLikedMovie(this);
        }
    }
    public String likeText(UserProfile user){
        return isMovieLikedByUser(user)? "Unlike" : "Like";
    }
}

