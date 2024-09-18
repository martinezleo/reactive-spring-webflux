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
        return ServerResponse.status(HttpStatus.OK)
            .body(reviewRepository.findAll().log(), Review.class);       
    
    }

    public Mono<ServerResponse> getReviewsByInfoId(ServerRequest request) {
        Long movieInfoId = Long.valueOf(request.queryParam("movieInfoId").get());
        return ServerResponse.status(HttpStatus.OK)
            .body(reviewRepository.findByMovieInfoId(movieInfoId).log(), Review.class);         
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        String reviewId = String.valueOf(request.pathVariable("id"));
        return request.bodyToMono(Review.class)
            .flatMap(inputReview -> {
                return reviewRepository.findById(reviewId).log()
                    .flatMap(origReview -> {
                        origReview.setMovieInfoId(inputReview.getMovieInfoId());
                        origReview.setComment(inputReview.getComment());
                        origReview.setRating(inputReview.getRating());
                        return reviewRepository.save(origReview).log();
                    });
            })
            .flatMap(monoReview -> 
                ServerResponse.status(HttpStatus.OK).bodyValue(monoReview)
            )
            .switchIfEmpty(ServerResponse.notFound().build());       
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        String reviewId = String.valueOf(request.pathVariable("id"));

        return reviewRepository.findById(reviewId).log()
            .hasElement()          
            .flatMap(hasElement -> {
                if(hasElement) {
                    return reviewRepository.deleteById(reviewId).log()
                    .flatMap(monoReview -> {
                        return ServerResponse.status(HttpStatus.NO_CONTENT).bodyValue(monoReview);
                    });
                }
                else {
                    return ServerResponse.status(HttpStatus.NOT_FOUND).build();
                }
            });
      
    }

}
