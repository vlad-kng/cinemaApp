package ru.dorin.cinemaAppBoot.controllers.admin.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rating {
    private Double kp;
    private Double imbd;
    private Double filmCritics;
    private Double russianFilmCritics;
    private Double await;

}
