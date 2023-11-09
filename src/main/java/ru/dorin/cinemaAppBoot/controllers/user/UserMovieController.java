package ru.dorin.cinemaAppBoot.controllers.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.dorin.cinemaAppBoot.models.Actor;
import ru.dorin.cinemaAppBoot.models.Director;
import ru.dorin.cinemaAppBoot.models.Movie;
import ru.dorin.cinemaAppBoot.services.ActorService;
import ru.dorin.cinemaAppBoot.services.DirectorService;
import ru.dorin.cinemaAppBoot.services.MoviesService;
import ru.dorin.cinemaAppBoot.util.DirectorValidator;
import ru.dorin.cinemaAppBoot.util.MovieValidator;

import java.util.List;

@Controller
@RequestMapping("/movies")
public class UserMovieController {
    private final MoviesService moviesService;
    private final ActorService actorService;
    private final DirectorService directorService;

    @Autowired
    public UserMovieController(MoviesService moviesService, ActorService actorService, DirectorService directorService) {
        this.moviesService = moviesService;
        this.actorService = actorService;
        this.directorService = directorService;
    }
    @GetMapping()
    public String index(Model model){
        model.addAttribute("movies", moviesService.findAll());
        return "user/movie/index";
    }
    @GetMapping("/{id}")
    public String show(Model model, @PathVariable("id") int id){
        Movie movie=moviesService.findOne(id);
        movie.setActors(actorService.findByMovie(id));
        moviesService.update(id, movie);
        model.addAttribute("movie", movie);
        model.addAttribute("actors", movie.getActors());
        return "user/movie/show";
    }

    @GetMapping("/search")
    public String searchPage(){
        return "user/movie/search";
    }
    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query){
        model.addAttribute("movies", moviesService.searchByName(query));
        return "user/movie/search";
    }
}
