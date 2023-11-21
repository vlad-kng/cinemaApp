package ru.dorin.cinemaAppBoot.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
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

    private static final String standartPoster = "https://yastatic.net/s3/kinopoisk-frontend/common-static/img/projector-logo/placeholder.svg";


    @Autowired
    public Consumer(MoviesService moviesService, ActorService actorService, DirectorService directorService, MovieValidator movieValidator) {
        this.moviesService = moviesService;
        this.actorService = actorService;
        this.directorService = directorService;
        this.movieValidator = movieValidator;
    }

    @SneakyThrows
    @GetMapping("/admin/get-movies")
    public String save(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("X-API-KEY", "35SXJ6P-9E7M16W-KTXVZ08-6NKWM6P");
        Map<String,Movie> movies = new HashMap();
        Map<String, Actor> actors = new HashMap<>();
        Map<String, Director> directors = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        for (int pageNumber = 1; pageNumber <= 4; pageNumber++) {
            String format = String.format("https://api.kinopoisk.dev/v1.4/movie?page=%d&limit=250&selectFields=name&selectFields=alternativeName&selectFields=description&selectFields=year&selectFields=rating&selectFields=genres&selectFields=poster&selectFields=persons&sortField=rating.kp&sortType=-1&type=movie&typeNumber=1&status=&rating.kp=8-10", pageNumber);
            HttpEntity<String> request = new HttpEntity<>(format, headers);
            String response = restTemplate.exchange(format, HttpMethod.GET, request, String.class).getBody();
            MovieList restMovies = mapper.readValue(response, MovieList.class);
            for (RestMovie restMovie : restMovies.getDocs()) {
                Movie movie = restMovieToMovie(restMovie);
                if(movie==null) continue;
                directors.put(movie.getDirectorName(), movie.getDirector());
                mergeActors(actors, movie.getActors()).forEach(actor -> actors.put(actor.getName(), actor));
                movies.put(movie.getName(), movie);
            }
        }
        moviesService.saveAll(movies.values());
        actorService.saveAll(actors.values());
        directorService.saveAll(directors.values());

        return "redirect:/admin";
    }

    private Movie restMovieToMovie(RestMovie restMovie){
        Director localDirector = new Director();
        List<Actor> localActorList = new ArrayList<>();

        String movieName = restMovie.getName();
        if (movieName == null || movieName.length()<=2 || movieName.length()>= 100) {
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
        Optional<String> optPoster = Optional.of(restMovie.getPosterUrl().orElse(standartPoster));

        Genre[] genres = restMovie.getGenres();
        if(genres.length==0) return null;
        String genre = genres[0].toString().split("=")[1];
        List<Person> people = restMovie.getPersons();
        for (Person person : people) {
            if (person.getName() == null) continue;
            if (person.getEnProfession().equalsIgnoreCase("actor")) {
                Actor actor = new Actor(person.getName());
                localActorList.add(actor);
            }
            if (person.getEnProfession().equalsIgnoreCase("director")) {
                localDirector.setName(person.getName());
            }
        }
        if(localDirector.getName()==null || localActorList.isEmpty())return null;
        Movie movie = new Movie(movieName, year, rate, info, localDirector, genre, optPoster.get());
        for (Actor actor : localActorList) {
            actor.addMovie(movie);
            movie.addActor(actor);
        }

      return movie;
    }

    private List<Actor> mergeActors(Map<String, Actor> actorMap, List<Actor> actorList){
        List<Actor> resultList = new ArrayList<>();
        for (Actor actor : actorList) {
            Set<Movie> movies = actor.getMovies();
            if(actorMap.containsKey(actor.getName())){
                actor = actorMap.get(actor.getName());
                movies.forEach(actor::addMovie);
            }
            resultList.add(actor);
        }
        return resultList;
    }
}
