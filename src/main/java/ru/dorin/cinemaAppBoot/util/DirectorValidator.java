package ru.dorin.cinemaAppBoot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.dorin.cinemaAppBoot.models.Director;
import ru.dorin.cinemaAppBoot.services.DirectorService;


@Component
public class DirectorValidator implements Validator {
    private  final DirectorService directorService;
    @Autowired
    public DirectorValidator(DirectorService directorService){
        this.directorService = directorService;
    }



    @Override
    public boolean supports(Class<?> clazz) {
        return Director.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Director director = (Director) target;
        if (directorService.findByName(director.getName()) != null) {
            errors.rejectValue("name", "", "This director is already taken");
        }
    }
        public void validateNewDirector(Object target, Errors errors) {
            Director director = (Director) target;
            if (directorService.findByName(director.getName()) != null) {
                director = directorService.findByName(director.getName()).get(0);
            }
            if(director == null){
                errors.rejectValue("name", "", "Must have a director");
            }


        }
}
