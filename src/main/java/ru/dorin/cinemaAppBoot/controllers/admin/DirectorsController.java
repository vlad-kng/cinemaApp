package ru.dorin.cinemaAppBoot.controllers.admin;

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
@RequestMapping("/admin/directors")
public class DirectorsController {
    private final MoviesService moviesService;
    private final DirectorService directorService;

    @Autowired
    public DirectorsController(MoviesService moviesService, DirectorService directorService) {
        this.moviesService = moviesService;
        this.directorService = directorService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("directors", directorService.findAll());
        return "admin/director/index";
    }
    @GetMapping("/{id}")
    public String show(Model model, @PathVariable("id") int id){
        Director director = directorService.findOne(id);
        Set<Movie> movies = director.getMovies();
        model.addAttribute("movies", movies);
        model.addAttribute("director", director);
        return "admin/director/show";
    }
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        Director director = directorService.findOne(id);
        Set<Movie> movies = director.getMovies();
        model.addAttribute("director", director);
        model.addAttribute("movies", movies);
        return "admin/director/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("actor") Director director,
                         @PathVariable("id") int id){
        directorService.update(id,director);
        return "redirect:/admin/directors/{id}";
    }
    @GetMapping("/{id}/{movieId}/remove")
    public String removeMovie(@ModelAttribute("director") Director director,
                              @PathVariable("id") int id,
                              @ModelAttribute("movie") Movie movie, @PathVariable("movieId") int movieId){
        director = directorService.findOne(id);
        director.removeMovie(movie);
        directorService.save(director);
        return "redirect:/admin/directors/{id}/edit";
    }
}
