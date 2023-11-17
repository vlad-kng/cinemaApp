package ru.dorin.cinemaAppBoot.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.dorin.cinemaAppBoot.models.user.UserProfile;
import ru.dorin.cinemaAppBoot.services.UserDetailsServiceImpl;

@Component
    public class UserValidator implements Validator {

        private final UserDetailsServiceImpl userDetailsService;

        @Autowired
        public UserValidator(UserDetailsServiceImpl userDetailsService) {
            this.userDetailsService = userDetailsService;
        }

        @Override
        public boolean supports(Class<?> aClass) {
            return UserProfile.class.equals(aClass);
        }

        @Override
        public void validate(Object o, Errors errors) {
            UserProfile user = (UserProfile) o;

            try {
                userDetailsService.loadUserByUsername(user.getUsername());
            } catch (UsernameNotFoundException ignored) {
                return; // все ок, пользователь не найден
            }

            errors.rejectValue("username", "", "Человек с таким именем пользователя уже существует");
        }
    }

