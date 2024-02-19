package ru.dorin.cinemaAppBoot.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import ru.dorin.cinemaAppBoot.models.Actor;
import ru.dorin.cinemaAppBoot.models.Director;
import ru.dorin.cinemaAppBoot.models.Movie;
import ru.dorin.cinemaAppBoot.rest.dto.*;
import ru.dorin.cinemaAppBoot.services.ActorService;
import ru.dorin.cinemaAppBoot.services.DirectorService;
import ru.dorin.cinemaAppBoot.services.MoviesService;
import ru.dorin.cinemaAppBoot.util.MovieValidator;

import java.util.*;

@Controller
public class Consumer {
    private final MoviesService moviesService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final MovieValidator movieValidator;

    private static final String standardPoster = "https://yastatic.net/s3/kinopoisk-frontend/common-static/img/projector-logo/placeholder.svg";

    Map<String, Movie> movies = new HashMap();
    Map<String, Director> directors = new HashMap();
    Map<String, Actor> actors = new HashMap();


    @Autowired
    public Consumer(MoviesService moviesService, ActorService actorService, DirectorService directorService, MovieValidator movieValidator) {
        this.moviesService = moviesService;
        this.actorService = actorService;
        this.directorService = directorService;
        this.movieValidator = movieValidator;
    }


    @GetMapping("/admin/get-movies")
    public String save() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("X-API-KEY", "35SXJ6P-9E7M16W-KTXVZ08-6NKWM6P");
        ObjectMapper mapper = new ObjectMapper();
        for (int pageNumber = 1; pageNumber <= 4; pageNumber++) {
            String format = String.format("https://api.kinopoisk.dev/v1.4/movie?page=%d&limit=250&selectFields=name&selectFields=alternativeName&selectFields=description&selectFields=year&selectFields=rating&selectFields=genres&selectFields=poster&selectFields=persons&sortField=rating.kp&sortType=-1&type=movie&typeNumber=1&status=&rating.kp=8-10", pageNumber);
            HttpEntity<String> request = new HttpEntity<>(format, headers);
            String response = restTemplate.exchange(format, HttpMethod.GET, request, String.class).getBody();
            MovieList restMovies = null;
            try {
                restMovies = mapper.readValue(response, MovieList.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            for (RestMovie restMovie : restMovies.getDocs()) {
                Movie movie = restMovieToMovie(restMovie);
                if (movie == null) continue;
                movies.put(movie.getName(), movie);
            }
        }
        mergeDirectors(directors, movies);
        mergeActors(actors, movies);
        moviesService.saveAll(movies.values());
        return "redirect:/admin";
    }

    private Movie restMovieToMovie(RestMovie restMovie) {
        Director localDirector = new Director();
        List<Actor> localActorList = new ArrayList<>();

        String movieName = restMovie.getName();
        if (movieName == null || movieName.length() <= 2 || movieName.length() >= 100) {
            return null;
        }
        int year = restMovie.getYear();
        String info = restMovie.getDescription();
        if (info == null || info == "") {
            return null;
        }
        double rate = restMovie.getRating().getKp();
        Poster p = restMovie.getPoster();
        if (p == null) return null;
        Optional<String> optPoster = Optional.of(restMovie.getPosterUrl().orElse(standardPoster));

        Genre[] genres = restMovie.getGenres();
        if (genres.length == 0) return null;
        String genre = genres[0].toString().split("=")[1];
        List<Person> people = restMovie.getPersons();
        for (Person person : people) {
            if (person.getName() == null) continue;
            if (person.getEnProfession().equalsIgnoreCase("actor")) {
                Actor actor = new Actor(person.getName());
                localActorList.add(actor);
                actors.put(actor.getName(), actor);
            }
            if (person.getEnProfession().equalsIgnoreCase("director")) {
                localDirector.setName(person.getName());
                directors.put(localDirector.getName(), localDirector);
            }
        }
        if (localDirector.getName() == null || localActorList.isEmpty()) return null;
        Movie movie = new Movie(movieName, year, rate, info, localDirector, genre, optPoster.get());
        for (Actor actor : localActorList) {
            actor.addMovie(movie);
            movie.addActor(actor);
        }

        return movie;
    }

    /* Сохраняем через Hibernate. этот метод не нужен.
    public void saveMovie(Movie movie) throws InterruptedException {
        Movie movieToSave = new Movie(movie); //создаем новый фильм из передаваемого, чтобы можно было итерироваться по movies
        Director director;
        List<Director> directors = directorService.findByName(movie.getDirectorName()); //проверяем есть ли режиссер в БД
        Thread.sleep(100);
        if (directors.size() >= 1) {
            director = directors.get(0);
        } else {
            director = new Director(movie.getDirectorName()); //если нет в БД создаем, и сохраняем в БД (присваиваем id)
            directorService.save(director);
            Thread.sleep(100);
        }
        movieToSave.setDirector(director); //связываем с двух сторон фильм и режиссера
        //director.addMovie(movieToSave);

        List<Actor> actorList = new ArrayList<>(movie.getActors()); // берем актеров из переданного фильма
        for (Actor actor : actorList) {
            Actor actorFromDB = actorService.findByName(actor.getName()); //проверяем их по БД
            Thread.sleep(100);
            if (actorFromDB != null) {
                actorFromDB.addMovie(movieToSave); //если есть в БД, связываем актера и фильм
                Thread.sleep(100);
                movieToSave.addActor(actorFromDB);
                System.out.printf("Aктер %s с id %d добавлен в фильм из ДБ. Фильм: %s \n", actorFromDB.getName(), actorFromDB.getId(), movieToSave.getName());
            } else {
                actor.removeMovie(movie); //если нет в БД, удаляем у актера переданный фильм, чтобы он не сохранялся в БД
                actorService.save(actor); //сохраняем актера, получаем id
                Thread.sleep(100);
                System.out.printf("Aктер %s с id %d сохранен в БД. Фильм: %s \n", actor.getName(), actor.getId(), movieToSave.getName());
                movieToSave.addActor(actor); //связываем актера и фильм
                //actor.addMovie(movieToSave);
            }
        }
        moviesService.save(movieToSave);//сохраняем фильм
        System.out.printf("фильм %s c id = %d сохранен в БД \n", movieToSave.getName(), movieToSave.getId());
        Thread.sleep(1000);
    }
    */

    public void mergeDirectors(Map<String, Director> directors, Map<String, Movie> movies) {
        for (Movie movie : movies.values()) {
            Director director = movie.getDirector();
            if (directors.containsKey(director.getName())) {
                director = directors.get(director.getName());
                director.addMovie(movie);
                movie.setDirector(director);
                directors.put(director.getName(), director);
            }
        }
    }

    public void mergeActors(Map<String, Actor> actors, Map<String, Movie> movies) {
        for (Movie movie : movies.values()) {
            List<Actor> actorList = new ArrayList<>(movie.getActors());

            if (actorList != null && actorList.size() > 0) {
                for (Actor actor : actorList) {
                    movie.removeActor(actor);
                    actor = actors.get(actor.getName());
                    actor.addMovie(movie);
                    movie.addActor(actor);
                    actors.put(actor.getName(), actor);
                }
            }
        }

    }
}