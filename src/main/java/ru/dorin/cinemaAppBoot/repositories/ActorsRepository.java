package ru.dorin.cinemaAppBoot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dorin.cinemaAppBoot.models.Actor;

import java.util.List;

@Repository
public interface ActorsRepository extends JpaRepository<Actor, Integer> {
    List<Actor> findByName(String name);
    @Query(value = "select a.id, a.name from actor a join movie_actor ma on a.id = ma.actor_id where ma.movie_id=:movieId", nativeQuery = true)
    List<Actor> findByMovie(@Param("movieId") int movieId);

}
