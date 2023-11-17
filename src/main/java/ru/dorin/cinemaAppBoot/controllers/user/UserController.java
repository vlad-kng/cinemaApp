package ru.dorin.cinemaAppBoot.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import ru.dorin.cinemaAppBoot.models.Movie;
import ru.dorin.cinemaAppBoot.models.user.UserProfile;
import ru.dorin.cinemaAppBoot.security.UserDetailsImpl;
import ru.dorin.cinemaAppBoot.services.MoviesService;
import ru.dorin.cinemaAppBoot.services.UserDetailsServiceImpl;

@Controller
public class UserController {

    private final UserDetailsServiceImpl userDetailsService;
    private final MoviesService moviesService;
    @Autowired
    public UserController(UserDetailsServiceImpl userDetailsService, MoviesService moviesService) {
        this.userDetailsService = userDetailsService;
        this.moviesService = moviesService;
    }
    @GetMapping("/personal-area")
    public String personalArea(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl authUser = (UserDetailsImpl) authentication.getPrincipal();
        UserProfile user = userDetailsService.findById(authUser.getId());
        model.addAttribute("user", user);
        return "user/personal-area";
    }
    @GetMapping("/personal-area/{movieId}/remove-watched-movie")
    public String removeWatchedMovie(@ModelAttribute("movie") Movie movie, @PathVariable("movieId") int movieId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl authUser = (UserDetailsImpl) authentication.getPrincipal();
        UserProfile user = userDetailsService.findById(authUser.getId());
        movie = moviesService.findOne(movieId);
        user.removeWatchedMovie(movie);
        userDetailsService.save(user);
        return "redirect:/personal-area";
    }


}
