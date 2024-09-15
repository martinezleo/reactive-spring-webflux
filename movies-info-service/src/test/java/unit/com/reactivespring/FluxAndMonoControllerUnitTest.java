package com.reactivespring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivespring.controller.FluxAndMonoController;

import reactor.test.StepVerifier;


@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerUnitTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux() {

        webTestClient.get()
            .uri("/movie-info/flux")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Integer.class)
            .hasSize(3);
    }

    @Test
    void fluxBody() {

        var flux = webTestClient.get()
            .uri("/movie-info/flux")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(Integer.class)
            .getResponseBody();

        StepVerifier.create(flux)
            .expectNext(1,2,3)
            .verifyComplete();
    }

    @Test
    void fluxBodyConsume() {

        webTestClient.get()
            .uri("/movie-info/flux")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Integer.class)
            .consumeWith( resultList -> {
                var body = resultList.getResponseBody();
                assert(body.size() == 3);
                assert(body.get(0).intValue() == 1);
                assert(body.get(1).intValue() == 2);
                assert(body.get(2).intValue() == 3);
            });

    }

    @Test
    void mono() {

        webTestClient.get()
            .uri("/movie-info/mono")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(String.class)
            .contains("Hello World!");
    }

    @Test
    void monoBody() {

        var mono = webTestClient.get()
            .uri("/movie-info/mono")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .returnResult(String.class)
            .getResponseBody();

        StepVerifier.create(mono)
            .expectNext("Hello World!")
            .verifyComplete();
    }

    @Test
    void monoBodyConsume() {

        webTestClient.get()
            .uri("/movie-info/mono")
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(String.class)
            .consumeWith( result -> {
                var body = result.getResponseBody();
                assert(body.equals("Hello World!"));
                assertEquals(body, "Hello World!");
            });

    }
}
