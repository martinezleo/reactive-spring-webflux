package com.reactivespring.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MoviesInfoRestClient {

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    private WebClient webClient;

    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieInfoId) {

        var url = moviesInfoUrl.concat("/{id}");

        log.info("MoviesInfo url from webClient: {}", url);

        return webClient.get()
            .uri(url, movieInfoId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()            
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                if(response.statusCode().equals(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()))) {
                    return Mono.error(new MoviesInfoClientException("Movie Info is not Available for id: " + movieInfoId, response.statusCode().value()));
                }
                return response.bodyToMono(String.class)
                    .flatMap(respMessage -> Mono.error(new MoviesInfoClientException(respMessage, response.statusCode().value())));
            })
            .onStatus(HttpStatusCode::is5xxServerError, serverResponse -> {
                return serverResponse.bodyToMono(String.class)
                    .flatMap(servMessage -> Mono.error(new MoviesInfoServerException("Server Exception in MovieInfoService: " + servMessage)));
            })
            .bodyToMono(MovieInfo.class)
            .log();

        }

        public Mono<MovieInfo> retrieveMovieInfoOld(String movieInfoId) {

            var url = moviesInfoUrl.concat("/{id}");
    
            log.info("MoviesInfo url from webClient: {}", url);
    
            return webClient.get()
                .uri(url, movieInfoId)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(MovieInfo.class);
                    }
                    return response.bodyToMono(String.class)
                            .flatMap(respMessage -> 
                                Mono.error(new MoviesInfoClientException(respMessage, response.statusCode().value())));

                })
                .log();
            }
    }

