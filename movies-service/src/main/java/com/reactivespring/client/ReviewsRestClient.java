package com.reactivespring.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewsRestClient {

    @Value("${restClient.reviewsUrl}")
    private String reviewsUrl;

    private WebClient webClient;

    public ReviewsRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Review> retrieveReviewsByInfoId(String movieInfoId) {

        var url = UriComponentsBuilder
            .fromHttpUrl(reviewsUrl)
            .queryParam("movieInfoId", Long.valueOf(movieInfoId))
            .buildAndExpand()
            .toString();

        log.info("Reviews url from webClient: {}", url);

        return webClient.get()
        .uri(url)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
            if(response.statusCode().equals(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()))) {
                return Mono.empty();
            }
            return response.bodyToMono(String.class)
                .flatMap(respMessage -> Mono.error(new ReviewsClientException(respMessage)));
        })
        .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                return serverResponse.bodyToMono(String.class)
                    .flatMap(servMessage -> Mono.error(new ReviewsServerException("Server Exception in ReviewsService: " + servMessage)));
            })
        .bodyToFlux(Review.class)
        .log();
    }

    public Flux<Review> retrieveReviewsByInfoIdOld(String movieInfoId) {

        var url = UriComponentsBuilder
            .fromHttpUrl(reviewsUrl)
            .queryParam("movieInfoId", Long.valueOf(movieInfoId))
            .buildAndExpand()
            .toString();

        log.info("Reviews url from webClient: {}", url);

        return webClient.get()
        .uri(url)
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToFlux(response -> {
            if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToFlux(Review.class);
            }
            else {
                return Flux.empty();
            }
        }).log();
    }
}
