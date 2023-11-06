package ru.dorin.cinemaAppBoot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dorin.cinemaAppBoot.models.user.Role;
import ru.dorin.cinemaAppBoot.models.user.UserProfile;
import ru.dorin.cinemaAppBoot.repositories.UserProfileRepository;

@Service
public class RegistrationService {
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public RegistrationService(UserProfileRepository userProfileRepository, PasswordEncoder passwordEncoder) {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public void register(UserProfile user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        userProfileRepository.save(user);
    }
}
