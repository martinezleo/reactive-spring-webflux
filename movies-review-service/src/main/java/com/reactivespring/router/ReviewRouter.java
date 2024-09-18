package com.reactivespring.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespring.handler.ReviewHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRouter(ReviewHandler reviewHandler) {

        return route()
                .GET("/v1/helloworld", (request -> ServerResponse.ok().bodyValue("Hello World!!")))
                .nest(RequestPredicates.path("/v1/reviews"), builder -> { 
                    builder.GET("", request -> reviewHandler.getAllReviews(request))
                           .POST("", request -> reviewHandler.addReview(request))
                           .PUT("/{id}", request -> reviewHandler.updateReview(request));
                })
                .build();
    }

}
