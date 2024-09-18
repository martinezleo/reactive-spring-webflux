package com.reactivespring.handler;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;

import reactor.core.publisher.Mono;

@Component
public class ReviewHandler {

    private ReviewReactiveRepository reviewRepository;

    public ReviewHandler(ReviewReactiveRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {

        return request.bodyToMono(Review.class)
            .flatMap(review -> 
                reviewRepository.save(review).log()
            )
            .flatMap(monoReview -> 
                ServerResponse.status(HttpStatus.CREATED).bodyValue(monoReview)
            );
        
    }

    public Mono<ServerResponse> getAllReviews(ServerRequest request) {

        return ServerResponse.status(HttpStatus.OK).body(reviewRepository.findAll().log(), Review.class);
        
    }

}
