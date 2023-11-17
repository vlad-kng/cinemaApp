package ru.dorin.cinemaAppBoot.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.dorin.cinemaAppBoot.models.Actor;
import ru.dorin.cinemaAppBoot.models.Movie;
import ru.dorin.cinemaAppBoot.services.ActorService;
import ru.dorin.cinemaAppBoot.services.MoviesService;

import java.util.Set;

@Controller
@RequestMapping("/actors")
public class UserActorsController {
    private final MoviesService moviesService;
    private final ActorService actorService;

    @Autowired
    public UserActorsController(MoviesService moviesService, ActorService actorService) {
        this.moviesService = moviesService;
        this.actorService = actorService;
    }

    @GetMapping("/{id}")
    public String show(Model model, @PathVariable("id") int id){
        Actor actor = actorService.findOne(id);
        Set<Movie> movies = actor.getMovies();
        model.addAttribute("movies", movies);
        model.addAttribute("actor", actor);
        return "user/actor/show";
    }
}
