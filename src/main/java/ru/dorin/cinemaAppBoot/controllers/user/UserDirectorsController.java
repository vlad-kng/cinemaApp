package ru.dorin.cinemaAppBoot.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.dorin.cinemaAppBoot.models.Director;
import ru.dorin.cinemaAppBoot.models.Movie;
import ru.dorin.cinemaAppBoot.services.DirectorService;
import ru.dorin.cinemaAppBoot.services.MoviesService;

import java.util.Set;

@Controller
@RequestMapping("/directors")
public class UserDirectorsController {
    private final MoviesService moviesService;
    private final DirectorService directorService;

    @Autowired
    public UserDirectorsController(MoviesService moviesService, DirectorService directorService) {
        this.moviesService = moviesService;
        this.directorService = directorService;
    }
    @GetMapping("/{id}")
    public String show(Model model, @PathVariable("id") int id){
        Director director = directorService.findOne(id);
        Set<Movie> movies = director.getMovies();
        model.addAttribute("movies", movies);
        model.addAttribute("director", director);
        return "user/director/show";
    }
}
