package ru.dorin.cinemaAppBoot.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person
{
    private int id;
    private String photo;
    private String name;
    private String enName;
    private String description;
    private String profession;
    private String enProfession;
}
