package ru.dorin.cinemaAppBoot.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.dorin.cinemaAppBoot.models.Movie;
import ru.dorin.cinemaAppBoot.models.user.UserProfile;
import ru.dorin.cinemaAppBoot.security.UserDetailsImpl;
import ru.dorin.cinemaAppBoot.services.ActorService;
import ru.dorin.cinemaAppBoot.services.DirectorService;
import ru.dorin.cinemaAppBoot.services.MoviesService;
import ru.dorin.cinemaAppBoot.services.UserDetailsServiceImpl;

@Controller
@RequestMapping("/movies")
public class UserMovieController {
    private final MoviesService moviesService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserMovieController(MoviesService moviesService, ActorService actorService, DirectorService directorService, UserDetailsServiceImpl userDetailsService) {
        this.moviesService = moviesService;
        this.actorService = actorService;
        this.directorService = directorService;
        this.userDetailsService = userDetailsService;
    }
    @GetMapping()
    public String index(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl authUser = (UserDetailsImpl) authentication.getPrincipal();
        UserProfile user = userDetailsService.findById(authUser.getId());
        model.addAttribute("user", user);
        model.addAttribute("movies", moviesService.findAllSortedByYearAndRate());

        return "user/movie/index";
    }

    @PatchMapping("/{id}/like")
    public String like(@ModelAttribute("movie") Movie movie, @PathVariable("id") int id){
        movie = moviesService.findOne(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl authUser = (UserDetailsImpl) authentication.getPrincipal();
        UserProfile user = userDetailsService.findById(authUser.getId());
        movie.like(user);
        moviesService.save(movie);
        userDetailsService.save(user);
        return "redirect:/movies/{id}";
    }

    @PatchMapping("/{id}/dislike")
    public String dislike(@ModelAttribute("movie") Movie movie, @PathVariable("id") int id){
        movie = moviesService.findOne(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl authUser = (UserDetailsImpl) authentication.getPrincipal();
        UserProfile user = userDetailsService.findById(authUser.getId());
        user.dislike(movie);
        moviesService.save(movie);
        userDetailsService.save(user);
        return "redirect:/movies/{id}";
    }

    @PatchMapping("/{id}/watched")
    public String watched(@ModelAttribute("movie") Movie movie, @PathVariable("id") int id){
        movie = moviesService.findOne(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl authUser = (UserDetailsImpl) authentication.getPrincipal();
        UserProfile user = userDetailsService.findById(authUser.getId());
        user.addWatchedMovie(movie);
        moviesService.save(movie);
        userDetailsService.save(user);
        return "redirect:/movies/{id}";
    }

    @GetMapping("/{id}")
    public String show(Model model, @PathVariable("id") int id){
        Movie movie=moviesService.findOne(id);
        movie.setActors(actorService.findByMovie(id));
        moviesService.update(id, movie);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl authUser = (UserDetailsImpl) authentication.getPrincipal();
        UserProfile user = userDetailsService.findById(authUser.getId());
        model.addAttribute("user", user);
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

//    public void like(Movie movie, UserProfile user){
//        boolean like = movie.isMovieLikedByUser(user);
//        if(!like){
//            movie.addUserWhoLiked(user);
//            user.addLikedMovie(movie);
//        }else{
//            movie.removeUserWhoLiked(user);
//            user.removeLikedMovie(movie);
//        }
//    }
}
