package ru.dorin.cinemaAppBoot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dorin.cinemaAppBoot.models.Director;
import ru.dorin.cinemaAppBoot.repositories.DirectorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {
    DirectorRepository directorRepository;

    @Autowired
    public DirectorService(DirectorRepository directorRepository){
        this.directorRepository=directorRepository;
    }

    public List<Director> findAll(){
        return directorRepository.findAll();
    }
    public Director findOne(int id){
        Optional<Director> director=directorRepository.findById(id);
        return director.orElse(null);
    }
    public void save(Director director){
        directorRepository.save(director);
    }
    public void save(String directorName){
        Director director = new Director(directorName);
        directorRepository.save(director);
    }
    public void saveAll(Iterable<Director> directors){directorRepository.saveAll(directors);}

    public void update(int id, Director director){
        director.setId(id);
        directorRepository.save(director);
    }
    public void delete(int id){
        directorRepository.deleteById(id);
    }

    public List<Director> findByName(String name) {
        return directorRepository.findByName(name);
    }
}


