package com.reactivespring.handler;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.repository.ReviewReactiveRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewHandler {

    @Autowired
    private Validator validator;
    
    private ReviewReactiveRepository reviewRepository;

    public ReviewHandler(ReviewReactiveRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    private void validate(Review review) {
        var contraintValidations = validator.validate(review);
        log.info("contraintValidations: {}", contraintValidations);
        if(contraintValidations.size() > 0) {
            var errorMessages = contraintValidations
                .stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .collect(Collectors.joining(","));

            throw new ReviewDataException(errorMessages);
        }
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {

        return request.bodyToMono(Review.class)
            .doOnNext(this::validate)
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
            .doOnNext(this::validate)
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
