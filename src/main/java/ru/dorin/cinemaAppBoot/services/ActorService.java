package ru.dorin.cinemaAppBoot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dorin.cinemaAppBoot.models.Actor;
import ru.dorin.cinemaAppBoot.repositories.ActorsRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ActorService {
    ActorsRepository actorsRepository;

    @Autowired
    public ActorService(ActorsRepository actorsRepository){
        this.actorsRepository=actorsRepository;
    }

    public List<Actor> findAll(){
        return actorsRepository.findAll();
    }
    public Actor findOne(int id){
        Optional<Actor> actor=actorsRepository.findById(id);
        return actor.orElse(null);
    }
    public void save(Actor actor){
        actorsRepository.save(actor);
    }
    public void save(String actorName){
        Actor actor = new Actor(actorName);
        actorsRepository.save(actor);
    }
    public void saveAll(Iterable<Actor> actors){actorsRepository.saveAll(actors);}
    public void update(int id, Actor updatedActor){
        updatedActor.setId(id);
        actorsRepository.save(updatedActor);
    }
    public void delete(int id){
        actorsRepository.deleteById(id);
    }

    public Actor findByName(String name) {
        Actor actor = null;
        try {
         actor = actorsRepository.findByName(name).get(0);
        } catch (Exception ex){ex.printStackTrace();}
    return actor;
    }
    public List<Actor> findByMovie(int movieId){
       return actorsRepository.findByMovie(movieId);
    }
}


