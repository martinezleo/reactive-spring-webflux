package com.reactivespring.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import com.reactivespring.domain.MovieInfo;

import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;


@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@ActiveProfiles("test")
public class MovieInfoRepositoryIntgTest {

    @Autowired
    MoviesInfoRepository movieInfoRepository;

    @BeforeEach
    void setup() {
        var movieInfos = List.of(
            new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        );

        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll() {

        var movieInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(movieInfoFlux)
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void findById() {

        var movieInfoMono = movieInfoRepository.findById("abc").log();

        StepVerifier.create(movieInfoMono)
            .assertNext(movieInfo -> {
                assertEquals("Dark Knight Rises", movieInfo.getName());
            })
            .verifyComplete();
    }

    @Test
    void findByYear() {

        var movieInfoMono = movieInfoRepository.findByYear(2012).log();

        StepVerifier.create(movieInfoMono)
            .assertNext(movieInfo -> {
                assertEquals("Dark Knight Rises", movieInfo.getName());
            })
            .verifyComplete();
    }

    @Test
    void saveMovieInfo() {

        var movie = new MovieInfo(null, "Batman Begins Again", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2015-06-15"));
        var movieInfoMono = movieInfoRepository.save(movie).log();

        StepVerifier.create(movieInfoMono)
            .assertNext(movieInfo -> {
                assertNotNull(movieInfo.getMovieInfoId());
                assertEquals("Batman Begins Again", movieInfo.getName());
            })
            .verifyComplete();
    }

    @Test
    void updateMovieInfo() {

        var movie = movieInfoRepository.findById("abc").block();
        movie.setYear(2021);
        movie.setName("Batman Begins Again II");

        var movieInfoMono = movieInfoRepository.save(movie).log();

        StepVerifier.create(movieInfoMono)
            .assertNext(movieInfo -> {
                assertEquals("Batman Begins Again II", movieInfo.getName());
                assertEquals(2021, movieInfo.getYear());
            })
            .verifyComplete();
    }

    @Test

    void deleteById() {

        movieInfoRepository.deleteById("abc").block();
        var movieInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(movieInfoFlux)
            .expectNextCount(2)
            .verifyComplete();
    }

}
