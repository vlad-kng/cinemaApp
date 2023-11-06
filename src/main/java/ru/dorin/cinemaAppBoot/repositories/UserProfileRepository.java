package ru.dorin.cinemaAppBoot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dorin.cinemaAppBoot.models.user.UserProfile;

import java.util.Optional;
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    Optional<UserProfile> findByUsername(String username);
}
