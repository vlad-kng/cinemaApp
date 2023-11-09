package ru.dorin.cinemaAppBoot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.dorin.cinemaAppBoot.models.user.UserProfile;
import ru.dorin.cinemaAppBoot.repositories.UserProfileRepository;
import ru.dorin.cinemaAppBoot.security.UserDetailsImpl;

import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserProfileRepository userRepository;
    @Autowired
    public UserDetailsServiceImpl(UserProfileRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<UserProfile> user = userRepository.findByUsername(s);

        if (user.isEmpty())
            throw new UsernameNotFoundException("User not found");

        return new UserDetailsImpl(user.get());
    }
        public List<UserProfile> findAll(){
        return userRepository.findAll();
    }
    public UserProfile findById(int id) {
        Optional<UserProfile> user = userRepository.findById(id);

        if (user.isEmpty())
            throw new RuntimeException("User not found");

        return user.get();
    }
    public void deleteById(int id){
        userRepository.deleteById(id);
    }

    public void save(UserProfile user) {
        userRepository.save(user);
    }
}
