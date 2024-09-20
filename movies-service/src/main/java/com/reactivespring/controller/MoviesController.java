package com.reactivespring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
@Slf4j
public class MoviesController {

    @Autowired
    private MoviesInfoRestClient moviesInfoRestClient;

    @Autowired
    private ReviewsRestClient reviewsRestClient;


    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {

       return moviesInfoRestClient.retrieveMovieInfo(movieId)
            .flatMap(movieInfo -> {
                var reviesListMono = reviewsRestClient
                        .retrieveReviewsByInfoId(movieId).collectList();
                return reviesListMono.map(reviewsList -> new Movie(movieInfo, reviewsList));
            });       
    }

}
