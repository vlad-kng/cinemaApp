package ru.dorin.cinemaAppBoot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dorin.cinemaAppBoot.models.Director;

import java.util.List;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Integer> {

    List<Director> findByName(String name);
}
