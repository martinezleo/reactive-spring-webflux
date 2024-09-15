package com.reactivespring.service;

import org.springframework.stereotype.Service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MoviesInfoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesInfoService {

    private MoviesInfoRepository moviesInfoRepository;

    // Injected Repository using Constructor instead of AutoWired annotation
    public MoviesInfoService(MoviesInfoRepository moviesInfoRepository) {
        this.moviesInfoRepository = moviesInfoRepository;
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return moviesInfoRepository.findAll().log();
    }

    public Mono<MovieInfo> getMovieInfo(String id) {
        return moviesInfoRepository.findById(id).log();
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        var monoMovieInfo = moviesInfoRepository.save(movieInfo).log();
        return monoMovieInfo;
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo movieInfo, String id) {
        var monoMovieInfo = moviesInfoRepository.findById(id).log()
            .flatMap(origMovieInfo -> {
                origMovieInfo.setName(movieInfo.getName());
                origMovieInfo.setCast(movieInfo.getCast());
                origMovieInfo.setYear(movieInfo.getYear());
                origMovieInfo.setReleaseDate(movieInfo.getReleaseDate());
                return moviesInfoRepository.save(origMovieInfo).log();

        });
        return monoMovieInfo;
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return moviesInfoRepository.deleteById(id).log();
    }

}
