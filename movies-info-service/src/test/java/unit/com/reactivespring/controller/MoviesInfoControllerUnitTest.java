package com.reactivespring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.time.LocalDate;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebClient
public class MoviesInfoControllerUnitTest {
 
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoServiceMock;

    static String MOVIES_INFO_URL = "/v1/movieinfos";

    
    @Test
    void getAllMovieInfos() {

        var movieInfos = List.of(
            new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "The Dark Knight", 2005, List.of("Christian Bale", "Heath Ledger"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "Dark Knight Rises", 2005, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        );
        Flux<MovieInfo> fluxMovieInfo = Flux.fromIterable(movieInfos);

        when(moviesInfoServiceMock.getAllMovieInfos())
            .thenReturn(fluxMovieInfo);

        webTestClient.get()
        .uri(MOVIES_INFO_URL)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(MovieInfo.class)
        .hasSize(3);

    }

    @Test
    void getMovieInfo() {

        var movieInfo = new MovieInfo("abc", "Dark Knight Rises", 2005, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        Mono<MovieInfo> monoInputMovieInfo = Mono.just(movieInfo);

        when(moviesInfoServiceMock.getMovieInfo("abc"))
            .thenReturn(monoInputMovieInfo);

        var monoMovieInfo = webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(MOVIES_INFO_URL + "/{id}")
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
    void getMovieInfoNotFound() {

        Mono<MovieInfo> monoNotFoundMovieInfo = Mono.empty();

        when(moviesInfoServiceMock.getMovieInfo("def"))
            .thenReturn(monoNotFoundMovieInfo);

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(MOVIES_INFO_URL + "/{id}")
                .build("def"))
            .exchange()
            .expectStatus()
            .isNotFound();

    }

    @Test
    void addMovieInfo() {

        var movieInfo = new MovieInfo("mockId", "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        Mono<MovieInfo> monoInputMovieInfo = Mono.just(movieInfo);

        when(moviesInfoServiceMock.addMovieInfo(movieInfo))
            .thenReturn(monoInputMovieInfo);

        webTestClient.post()
            .uri(MOVIES_INFO_URL)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MovieInfo.class)
            .consumeWith( resultBody -> {
                var result = resultBody.getResponseBody();
                assertEquals("mockId", result.getMovieInfoId());
                assertEquals("Batman Begins", result.getName());
            });

    }

    @Test
    void addMovieInfoInputValid() {

        var movieInfo = new MovieInfo("mockId", "", -2005, List.of(""), LocalDate.parse("2005-06-15"));
        Mono<MovieInfo> monoInputMovieInfo = Mono.just(movieInfo);
        String expectedError = "movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be a valid year";

        when(moviesInfoServiceMock.addMovieInfo(movieInfo))
            .thenReturn(monoInputMovieInfo);

        webTestClient.post()
            .uri(MOVIES_INFO_URL)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String.class)
            .consumeWith( resultBody -> {
                var result = resultBody.getResponseBody();
                assertNotNull(result);
                assertEquals(result, expectedError);
                System.out.println(result);
            });

    }

    @Test
    void updateMovieInfo() {

        var movieInfo = new MovieInfo("abc", "Updated Dark Knight Rises", 2024, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2024-07-20"));
        Mono<MovieInfo> monoInputMovieInfo = Mono.just(movieInfo);

        when(moviesInfoServiceMock.updateMovieInfo(movieInfo, "abc"))
            .thenReturn(monoInputMovieInfo);

        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path(MOVIES_INFO_URL + "/{id}")
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

    @Test
    void updateMovieInfoNotFound() {

        var movieInfo = new MovieInfo("abc", "Updated Dark Knight Rises", 2024, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2024-07-20"));
        Mono<MovieInfo> monoNotFoundMovieInfo = Mono.empty();

        when(moviesInfoServiceMock.updateMovieInfo(movieInfo, "def"))
            .thenReturn(monoNotFoundMovieInfo);

        webTestClient.put()
            .uri(uriBuilder -> uriBuilder
                .path(MOVIES_INFO_URL + "/{id}")
                .build("def"))
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isNotFound();

    }

    @Test
    void deleteMovieInfos() {

        Mono<Void> monoInputMovieInfo = Mono.empty();

        when(moviesInfoServiceMock.deleteMovieInfo("abc"))
            .thenReturn(monoInputMovieInfo);

        webTestClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path(MOVIES_INFO_URL + "/{id}")
                .build("abc"))
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Void.class)
            .consumeWith( resultBody -> {
                var result = resultBody.getResponseBody();
                assertNull(result);
            });
    }

}
