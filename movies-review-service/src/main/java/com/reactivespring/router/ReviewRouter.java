package com.reactivespring.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespring.handler.ReviewHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RequestPredicates.queryParam;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRouter(ReviewHandler reviewHandler) {

        return route()
                .GET("/v1/helloworld", (request -> ServerResponse.ok().bodyValue("Hello World!!")))
                .nest(path("/v1/reviews"), builder -> { 
                    builder
                        .GET("", queryParam("movieInfoId", movieInfoId -> !movieInfoId.isEmpty()), request -> reviewHandler.getReviewsByInfoId(request))
                        .GET("", request -> reviewHandler.getAllReviews(request))
                        .POST("", request -> reviewHandler.addReview(request))
                        .PUT("/{id}", request -> reviewHandler.updateReview(request))
                        .DELETE("/{id}", request -> reviewHandler.deleteReview(request));
                })
                .build();
    }

}
