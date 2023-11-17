package ru.dorin.cinemaAppBoot.controllers.admin;

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
@RequestMapping("/admin/movies")
public class MoviesController {
    private final MoviesService moviesService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final MovieValidator movieValidator;
    private final DirectorValidator directorValidator;
    @Autowired
    public MoviesController(MoviesService moviesService, ActorService actorService, DirectorService directorService, MovieValidator movieValidator, DirectorValidator directorValidator) {
        this.moviesService = moviesService;
        this.actorService = actorService;
        this.directorService = directorService;
        this.movieValidator = movieValidator;
        this.directorValidator = directorValidator;
    }
    @GetMapping()
    public String index(Model model){
        model.addAttribute("movies", moviesService.findAllSortedByYearAndRate());
        return "admin/movie/index";
    }
    @GetMapping("/{id}")
    public String show(Model model, @PathVariable("id") int id){
        Movie movie=moviesService.findOne(id);
        movie.setActors(actorService.findByMovie(id));
        moviesService.update(id, movie);
        model.addAttribute("movie", movie);
        model.addAttribute("actors", movie.getActors());
        return "admin/movie/show";
    }
    @GetMapping("/new")
    public String newMovie(@ModelAttribute("movie")Movie movie){
        return "admin/movie/new";
    }

    @PostMapping("/new/add")
    public String create(@ModelAttribute("movie") Movie movie){
        List<Director> directors = directorService.findByName(movie.getDirectorName());
        Director director = new Director();
        if(directors.size()<1){
            director.setName(movie.getDirectorName());
            movie.setDirector(director);
            directorService.save(director);
        } else{ director = directors.get(0);
        movie.setDirector(director);}
        String[] actorsName = movie.getActorsName();
        for (String name : actorsName) {
            Actor actor = new Actor(name);
            Actor actorFromDB = actorService.findByName(actor.getName());

            if (actorFromDB != null){
                actorFromDB.addMovie(movie);
                movie.addActor(actorFromDB);
            }else {
                movie.addActor(actor);
                actor.addMovie(movie);
                actorService.save(actor);
            }
            }
        movie.setDirector(director);
        moviesService.save(movie);
        return "redirect:/admin/movies";
    }
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("movie", moviesService.findOne(id));
        return "admin/movie/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("movie") @Valid Movie movie,
                         BindingResult bindingResult, @PathVariable("id") int id){
        movieValidator.validate(movie, bindingResult);
        if(bindingResult.hasErrors())
            return "admin/movie/edit";

        moviesService.update(id,movie);
        return "redirect:/admin/movies";
    }
    @GetMapping("/{id}/{actorId}/remove")
    public String removeActor(@ModelAttribute("actor") Actor actor,
                              @PathVariable("id") int id,
                              @ModelAttribute("movie") Movie movie, @PathVariable("actorId") int actorId){
        movie = moviesService.findOne(id);
        actor = actorService.findOne(actorId);
        actor.removeMovie(movie);
        actorService.save(actor);
        return "redirect:/admin/movies/{id}/edit";
    }
    @GetMapping("/{id}/addActor")
    public String addActor(Model model, @PathVariable("id") int id, @ModelAttribute("actor") Actor actor){
        Movie movie = moviesService.findOne(id);
        List<Actor> movieActors = movie.getActors();
        List<Actor> actors = actorService.findAll();
        actors.removeAll(movieActors);
        model.addAttribute("movie", movie);
        model.addAttribute("actors", actors);
        return "admin/movie/addActor";
    }
    @PatchMapping("/{id}/addActor/add")
    public String add(Model model, @PathVariable("id") int id, @ModelAttribute("actor") Actor actor){
        Movie movie = moviesService.findOne(id);
        actor=actorService.findOne(actor.getId());
        actor.addMovie(movie);
        movie.addActor(actor);
        moviesService.save(movie);
        actorService.save(actor);
        model.addAttribute("movie", movie);
        model.addAttribute("actor", actor);
        return "redirect:/admin/movies/{id}";
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable("id") int id){
        Movie movie = moviesService.findOne(id);
        List<Actor> actors = movie.getActors();
        for (Actor actor : actors) {
            actor.removeMovie(movie);
            actorService.save(actor);
        }
//        moviesService.removeActorsFromMovie(id);
//        movie=movie.removeActors();
//        moviesService.save(movie);
        moviesService.delete(movie);
        return "redirect:/admin/movies";
    }

    @GetMapping("/search")
    public String searchPage(){
        return "admin/movie/search";
    }
    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query){
        model.addAttribute("movies", moviesService.searchByName(query));
        return "admin/movie/search";
    }
}
