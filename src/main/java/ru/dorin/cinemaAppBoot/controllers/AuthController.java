package ru.dorin.cinemaAppBoot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.dorin.cinemaAppBoot.models.user.Role;
import ru.dorin.cinemaAppBoot.models.user.UserProfile;
import ru.dorin.cinemaAppBoot.services.RegistrationService;
import ru.dorin.cinemaAppBoot.util.UserValidator;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserValidator userValidator;
    private final RegistrationService registrationService;

    @Autowired
    public AuthController(UserValidator userValidator, RegistrationService registrationService) {
        this.userValidator = userValidator;
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "auth/login";
    }

    @PostMapping("/process_login")
    public void login(){};

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("user") UserProfile user) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("user") @Valid UserProfile user,
                                      BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors())
            return "/auth/registration";
        registrationService.register(user);

        return "redirect:/auth/login";
    }
}
