package ru.dorin.cinemaAppBoot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.dorin.cinemaAppBoot.models.Movie;
import ru.dorin.cinemaAppBoot.services.MoviesService;

@Component
public class MovieValidator implements Validator
{
    private final MoviesService moviesService;

    @Autowired
    public MovieValidator(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Movie.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Movie movie = (Movie) target;

        if (moviesService.findByName(movie.getName()) != null) {
            errors.rejectValue("name", "", "This name is already taken");
        }
        if(!(movie.getYearOfProduction()>=1965)){
            errors.rejectValue("yearOfProduction", "", "The first film was made in 1965");
        }
        if(movie.getDirector() == null){
            errors.rejectValue("director", "", "Movie must have director");
        }
        if(movie.getGenre() == null){
            errors.rejectValue("genre", "", "Movie must have genre");
        }
    }
//    public void validateDeleteHandler(Object target, Errors errors) {
//        Movie movie = (Movie) target;
//        if (book.getBookHandlerId() == 0) {
//            errors.rejectValue("bookHandlerId", "", "This book is not taken");
//        }
//    }
}
