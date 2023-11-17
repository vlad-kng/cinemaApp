package ru.dorin.cinemaAppBoot.controllers.admin.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import ru.dorin.cinemaAppBoot.controllers.admin.rest.model.*;
import ru.dorin.cinemaAppBoot.models.Actor;
import ru.dorin.cinemaAppBoot.models.Director;
import ru.dorin.cinemaAppBoot.models.Movie;
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
        for (int pageNumber = 5; pageNumber <= 7; pageNumber++) {
            String format = String.format("https://api.kinopoisk.dev/v1.4/movie?page=%d&limit=250&selectFields=name&selectFields=alternativeName&selectFields=description&selectFields=year&selectFields=rating&selectFields=genres&selectFields=poster&selectFields=persons&sortField=rating.kp&sortType=-1&type=movie&typeNumber=1&status=&rating.kp=8-10", pageNumber);
            HttpEntity<String> request = new HttpEntity<>(format, headers);
            String response = restTemplate.exchange(format, HttpMethod.GET, request, String.class).getBody();
            MovieList restMovies = mapper.readValue(response, MovieList.class);
            for (RestMovie restMovie : restMovies.getDocs()) {
                String movieName = restMovie.getName();
                if (movieName == null || movieName.length()<=2 || movieName.length()>= 100) {
                    continue;
                }
                int year = restMovie.getYear();
                String info = restMovie.getDescription();
                if (info == null || info == "") {
                    continue;
                }
                double rate = restMovie.getRating().getKp();
                Poster p = restMovie.getPoster();
                if (p == null) continue;
                String standartPoster = "https://yastatic.net/s3/kinopoisk-frontend/common-static/img/projector-logo/placeholder.svg";
                Optional<String> optPoster = Optional.of(restMovie.getPosterUrl().orElse(standartPoster));
               // String poster = restMovie.getPoster().getUrl() == null? stndPoster : restMovie.getPoster().getUrl();
//                if (poster == null) {
//                    poster = "https://yastatic.net/s3/kinopoisk-frontend/common-static/img/projector-logo/placeholder.svg";
//                }
                Genre[] genres = restMovie.getGenres();
                if(genres.length==0)continue;
                String genre = genres[0].toString().split("=")[1];
                Director localDirector = new Director();
                List<Actor> localActorList = new ArrayList<>();
                List<Person> people = restMovie.getPersons();
                for (Person person : people) {
                    if (person.getName() == null) continue;
                    if (person.getEnProfession().equalsIgnoreCase("actor")) {
                        Actor actor = new Actor(person.getName());
                        localActorList.add(actor);
                    }
                    if (person.getEnProfession().equalsIgnoreCase("director")) {
                        localDirector.setName(person.getName());
                        directors.put(localDirector.getName(), localDirector);
                    }
                }
                if(localDirector.getName()==null || localActorList.isEmpty())continue;
                Movie movie = new Movie(movieName, year, rate, info, localDirector, genre, optPoster.get());
                for (Actor actor : localActorList) {
                    if(actors.containsKey(actor.getName())){
                        actor=actors.get(actor.getName());
                    }
                    actor.addMovie(movie);
                    movie.addActor(actor);
                    actors.put(actor.getName(), actor);
                }

                movies.put(movie.getName(), movie);
            }
        }


        moviesService.saveAll(movies.values());
        actorService.saveAll(actors.values());
        directorService.saveAll(directors.values());

        return "redirect:/admin";
    }

}
