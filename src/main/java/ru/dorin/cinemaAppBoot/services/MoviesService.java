package ru.dorin.cinemaAppBoot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dorin.cinemaAppBoot.models.Actor;
import ru.dorin.cinemaAppBoot.models.Movie;
import ru.dorin.cinemaAppBoot.repositories.MoviesRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MoviesService {
    private MoviesRepository moviesRepository;

    @Autowired
    public MoviesService(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }
    public List<Movie> findAll(){
        return moviesRepository.findAll();
    }
    public Movie findOne(int id){
        Optional<Movie> movie=moviesRepository.findById(id);
        return movie.orElse(null);
    }
    @Transactional
    public void save(Movie movie){
        moviesRepository.save(movie);
    }
    @Transactional
    public void update(int id, Movie updatedMovie){
        updatedMovie.setId(id);
        moviesRepository.save(updatedMovie);
    }
    @Transactional
    public void delete(int id){
        moviesRepository.deleteById(id);
    }
    @Transactional
    public void delete(Movie movie){
        moviesRepository.delete(movie);
    }
    @Transactional
    public void removeActorsFromMovie(int movieId){
        moviesRepository.removeActorsFromMovie(movieId);
    }

    public Movie findByName(String name) {
        return moviesRepository.findByName(name).get(0);
    }

    public List<Movie> searchByName(String query) {
        return moviesRepository.searchByNameContainingIgnoreCase(query);
    }
}
