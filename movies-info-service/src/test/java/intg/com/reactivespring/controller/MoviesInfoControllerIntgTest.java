package com.reactivespring.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MoviesInfoRepository;

import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class MoviesInfoControllerIntgTest {

    @Autowired
    MoviesInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        var movieInfos = List.of(
            new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "The Dark Knight", 2005, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "Dark Knight Rises", 2005, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        );

        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void getAllMovieInfos() {

        webTestClient.get()
            .uri("/v1/movieinfos")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(MovieInfo.class)
            .hasSize(3);
    }

    @Test
    void getMovieInfo() {

        var monoMovieInfo = webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1/movieinfos/{id}")
                .build("abc"))
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(MovieInfo.class)
            .getResponseBody();

        StepVerifier.create(monoMovieInfo)
            .assertNext(result -> {
                assertEquals("abc", result.getMovieInfoId());
                assertEquals("Dark Knight Rises", result.getName());
            })
            .verifyComplete();
    }

    @Test
    void getMovieInfoJson() {

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1/movieinfos/{id}")
                .build("abc"))
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Dark Knight Rises");
    }

    @Test
    void addMovieInfo() {

        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        var monoMovieInfo = webTestClient.post()
            .uri("/v1/movieinfos")
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isCreated()
            .returnResult(MovieInfo.class)
            .getResponseBody();

        StepVerifier
            .create(monoMovieInfo)
            .assertNext( result -> {
                assertNotNull(result.getMovieInfoId());
            })
            .verifyComplete();

    }

    @Test
    void addMovieInfoConsume() {
        
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
                
        webTestClient.post()
            .uri("/v1/movieinfos")
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MovieInfo.class)
            .consumeWith( resultBody -> {
                var result = resultBody.getResponseBody();
                assertNotNull(result.getMovieInfoId());
            });

    }

    @Test
    void updateMovieInfo() {

        var movieInfo = new MovieInfo("abc", "Updated Dark Knight Rises", 2024, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2024-07-20"));

        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/v1/movieinfos/{id}")
                .build("abc"))
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(MovieInfo.class)
            .consumeWith( resultBody -> {
                var result = resultBody.getResponseBody();
                assertEquals("abc", result.getMovieInfoId());
                assertEquals("Updated Dark Knight Rises", result.getName());
                assertEquals(2024, result.getYear());
                assertEquals(LocalDate.parse("2024-07-20"), result.getReleaseDate());
                assertEquals(List.of("Christian Bale", "Tom Hardy"), result.getCast());
            });

    }

}
