package com.reactivespring.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class MovieInfo {

    @Id
    private String movieInfoId;
    @NotBlank(message = "movieInfo.name must be present")
    private String name;
    @NotNull
    @Positive(message = "movieInfo.year must be a valid year")
    private Integer year;
    private List<@NotBlank(message = "movieInfo.cast must be present") String> cast;
    private LocalDate releaseDate;

}
