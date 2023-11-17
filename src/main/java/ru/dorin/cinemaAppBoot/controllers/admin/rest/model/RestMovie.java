package ru.dorin.cinemaAppBoot.controllers.admin.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestMovie {
    private String name;
    private String alternativeName;
    private Integer year;
    private String description;
    private Rating rating;
    private Poster poster;
    private Genre[] genres;
    private List<Person> persons;

    public Optional<String> getPosterUrl(){
        return Optional.ofNullable(poster.getUrl());
    }
}
