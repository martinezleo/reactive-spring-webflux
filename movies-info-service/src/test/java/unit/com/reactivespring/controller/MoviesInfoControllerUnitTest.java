package com.reactivespring.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;

import reactor.core.publisher.Flux;

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

}
