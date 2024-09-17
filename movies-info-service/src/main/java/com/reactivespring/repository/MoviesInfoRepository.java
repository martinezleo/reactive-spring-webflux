package com.reactivespring.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.reactivespring.domain.MovieInfo;

import reactor.core.publisher.Flux;


public interface MoviesInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {
    Flux<MovieInfo> findByYear(Integer year);
    Flux<MovieInfo> findByName(String name);
}
