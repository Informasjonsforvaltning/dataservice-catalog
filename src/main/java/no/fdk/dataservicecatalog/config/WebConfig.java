package no.fdk.dataservicecatalog.config;

import no.fdk.dataservicecatalog.controller.DataServiceRegistrationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class WebConfig {

    @Bean
    public RouterFunction<ServerResponse> dataServiceRegistrationRouter(DataServiceRegistrationHandler dataServiceRegistrationHandler) {
        return route(GET("/catalogs/{catalogId}/dataservices"), dataServiceRegistrationHandler::all)
                .and(route(POST("/catalogs/{catalogId}/dataservices"), dataServiceRegistrationHandler::create))
                .and(route(GET("/catalogs/{catalogId}/dataservices/{dataServiceId}"), dataServiceRegistrationHandler::get))
                .and(route(PATCH("/catalogs/{catalogId}/dataservices/{dataServiceId}"), dataServiceRegistrationHandler::patch))
                .and(route(DELETE("/catalogs/{catalogId}/dataservices/{dataServiceId}"), dataServiceRegistrationHandler::delete))

                .and(route(POST("/catalogs/{catalogId}/dataservices/import"), dataServiceRegistrationHandler::importByUrl))
                .and(route(POST("/catalogs/{catalogId}/dataservices/{dataServiceId}/import"), dataServiceRegistrationHandler::editByUrl));
    }
}
