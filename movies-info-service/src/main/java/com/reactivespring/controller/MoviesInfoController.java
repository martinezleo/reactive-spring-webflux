package com.reactivespring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private MoviesInfoService moviesInfoService;
    
    // Inject Service using constructor instead of AuroWired annotation
    public MoviesInfoController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
    }

    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovieInfos() {
        
        return moviesInfoService.getAllMovieInfos();
    }

    @GetMapping(value = "/movieinfos", params = "year")
    public Flux<MovieInfo> getMovieInfosByYear(@RequestParam(value = "year", required = true) Integer year) {
        
        return moviesInfoService.getMovieInfosByYear(year);
    }

    @GetMapping(value = "/movieinfos", params = "name")
    public Flux<MovieInfo> getMovieInfosByName(@RequestParam(value = "name", required = true) String name) {
        
        return moviesInfoService.getMovieInfosByName(name);
    }

    @GetMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfo(@PathVariable String id) {
        return moviesInfoService.getMovieInfo(id)
            .map(ResponseEntity.ok()::body)
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo);
    }

    @PutMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody @Valid MovieInfo movieInfo, @PathVariable String id) {
        return moviesInfoService.updateMovieInfo(movieInfo, id)
                    .map(ResponseEntity.ok()::body)
                    .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return moviesInfoService.deleteMovieInfo(id);
    }
}
