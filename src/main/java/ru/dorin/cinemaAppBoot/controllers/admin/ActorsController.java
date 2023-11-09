package ru.dorin.cinemaAppBoot.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.dorin.cinemaAppBoot.models.Actor;
import ru.dorin.cinemaAppBoot.models.Movie;
import ru.dorin.cinemaAppBoot.services.ActorService;
import ru.dorin.cinemaAppBoot.services.MoviesService;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("admin/actors")
public class ActorsController {
    private final MoviesService moviesService;
    private final ActorService actorService;

    @Autowired
    public ActorsController(MoviesService moviesService, ActorService actorService) {
        this.moviesService = moviesService;
        this.actorService = actorService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("actors", actorService.findAll());
        return "admin/actor/index";
    }
    @GetMapping("/{id}")
    public String show(Model model, @PathVariable("id") int id){
        Actor actor = actorService.findOne(id);
        Set<Movie> movies = actor.getMovies();
        model.addAttribute("movies", movies);
        model.addAttribute("actor", actor);
        return "admin/actor/show";
    }
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        Actor actor = actorService.findOne(id);
        Set<Movie> movies = actor.getMovies();
        model.addAttribute("actor", actor);
        model.addAttribute("movies", movies);
        return "admin/actor/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("actor") Actor actor,
                         @PathVariable("id") int id){
        actorService.update(id,actor);
        return "redirect:/admin/actors";
    }
    @GetMapping("/{id}/{movieId}/remove")
    public String removeMovie(@ModelAttribute("actor") Actor actor,
                              @PathVariable("id") int id,
                              @ModelAttribute("movie") Movie movie, @PathVariable("movieId") int movieId){
        actor = actorService.findOne(id);
        actor.removeMovie(movie);
        actorService.save(actor);
        return "redirect:/admin/actors/{id}/edit";
    }
}
