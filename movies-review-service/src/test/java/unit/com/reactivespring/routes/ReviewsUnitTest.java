package com.reactivespring.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;

import reactor.core.publisher.Mono;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebClient
public class ReviewsUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReviewReactiveRepository reviewRepositoryMock;

    static String REVIEWS_URL = "/v1/reviews";


    @Test
    void addReview() {
                
        var review = new Review("mockId", 1L, "Movie was exhilarating", 5.0);        
        Mono<Review> monoReview = Mono.just(review);

        when(reviewRepositoryMock.save(review))
            .thenReturn(monoReview);

        webTestClient.post()
            .uri(REVIEWS_URL)
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Review.class)
            .consumeWith( resultBody -> {
                var result = resultBody.getResponseBody();
                assertEquals("mockId", result.getReviewId());
                assertEquals("Movie was exhilarating", result.getComment());
            });
    }

    @Test
    void addReviewValidation() {

        var review = new Review("mockId", null, "Movie was exhilarating", -5.0);        
        Mono<Review> monoReview = Mono.just(review);

        String expectedError = "movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be a valid year";

        when(reviewRepositoryMock.save(review))
            .thenReturn(monoReview);

        webTestClient.post()
            .uri(REVIEWS_URL)
            .bodyValue(review)
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
}
