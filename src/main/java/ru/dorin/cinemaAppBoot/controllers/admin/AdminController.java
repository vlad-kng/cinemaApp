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
@RequestMapping("/admin")
public class AdminController {

    public AdminController(){}

    @GetMapping()
    public String adminPage(){
        return "/admin/admin";
    }

}
