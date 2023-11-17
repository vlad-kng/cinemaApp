package ru.dorin.cinemaAppBoot.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.dorin.cinemaAppBoot.models.user.Role;
import ru.dorin.cinemaAppBoot.models.user.UserProfile;
import ru.dorin.cinemaAppBoot.services.MoviesService;
import ru.dorin.cinemaAppBoot.services.UserDetailsServiceImpl;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final MoviesService moviesService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AdminController(MoviesService moviesService, UserDetailsServiceImpl userDetailsService){
        this.moviesService = moviesService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping()
    public String adminPage(Model model){
        model.addAttribute("users", userDetailsService.findAll().size());
        model.addAttribute("movies", moviesService.findAll().size());


        return "/admin/admin";
    }
    @GetMapping("/users")
    public String getUsers(Model model){
        model.addAttribute("users", userDetailsService.findAll());
        return "/admin/user/index";
    }

    @GetMapping("/users/{id}")
    public String showUser(Model model, @PathVariable("id") int id){
        UserProfile user = userDetailsService.findById(id);
        model.addAttribute("user", user);
        return "admin/user/show";
    }
    @PatchMapping("/users/{id}/setRole")
    public String setRole(@ModelAttribute("user") UserProfile user, @PathVariable("id") int id){
        user = userDetailsService.findById(id);
        Role roleToSet = user.getRole().equals(Role.ROLE_USER)? Role.ROLE_ADMIN : Role.ROLE_USER;
        user.setRole(roleToSet);
        userDetailsService.save(user);
        return "redirect:/admin/users/{id}";
    }


    @DeleteMapping("/users/{id}/delete")
    public String deleteUser(@ModelAttribute("user") UserProfile user,
                              @PathVariable("id") int id){
        userDetailsService.deleteById(id);

        return "redirect:/admin/users";
    }

}
