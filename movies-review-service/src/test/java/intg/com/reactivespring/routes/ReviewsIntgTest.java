package com.reactivespring.routes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;

import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    @Autowired
    ReviewReactiveRepository reviewRepository;

    @Autowired
    WebTestClient webTestClient;

    static String REVIEWS_URL = "/v1/reviews";
    
    @BeforeEach
    void setup() {
        List<Review> movieReviews = List.of(
            new Review(null, 1L, "Movie was exhilarating", 5.0),
            new Review(null, 2L, "Movie even more exciting than original", 5.0));
    
        reviewRepository.saveAll(movieReviews).log().blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll().log().block();
    }

    @Test
    void getAllReviews() {

        webTestClient.get()
            .uri(REVIEWS_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class)
            .consumeWith( resultList -> {
                var body = resultList.getResponseBody();
                assert(body.size() == 2);
                var fluxReview = body.get(0);
                assertNotNull(fluxReview.getReviewId());
                assertEquals(1L, fluxReview.getMovieInfoId());
                assertEquals("Movie was exhilarating", fluxReview.getComment());
                assertEquals(5.0, fluxReview.getRating());
                fluxReview = body.get(1);
                assertNotNull(fluxReview.getReviewId());
                assertEquals(2L, fluxReview.getMovieInfoId());
                assertEquals("Movie even more exciting than original", fluxReview.getComment());
                assertEquals(5.0, fluxReview.getRating());

            });


    }

    @Test
    void addreview() {

        var review = new Review(null, 1L, "Awesome movie!!", 5.0);
        var monoReview = webTestClient.post()
            .uri(REVIEWS_URL)
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isCreated()
            .returnResult(Review.class)
            .getResponseBody();

        StepVerifier
            .create(monoReview)
            .assertNext(result -> {
                assertNotNull(result.getReviewId());
                assertEquals(1L, result.getMovieInfoId());
                assertEquals("Awesome movie!!", result.getComment());
                assertEquals(5.0, result.getRating());
            })
            .verifyComplete();
    }
}
