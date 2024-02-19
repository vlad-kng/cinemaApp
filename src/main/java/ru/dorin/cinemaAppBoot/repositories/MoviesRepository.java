package ru.dorin.cinemaAppBoot.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dorin.cinemaAppBoot.models.Movie;

import java.util.List;

@Repository
public interface MoviesRepository extends JpaRepository<Movie,Integer> {
    List<Movie> findByName(String name);
    @Query(value = "delete from movie_actor where movie_id=:movieID", nativeQuery = true)
    void removeActorsFromMovie(@Param("movieID") int movieID);

//    List<Movie> searchByNameIgnoreCaseStartingWith(String query);

    List<Movie> searchByNameContainingIgnoreCase(String query);

    @Query(value = "insert into movie(director_id,movie_genre,info,like_count,name,poster,rate,year_of_production) values (?, ?, ?, ?,?,?,?,?) ON CONFLICT DO NOTHING;", nativeQuery = true)
    void saveAllWithConflict(Iterable<Movie> movies);


}
