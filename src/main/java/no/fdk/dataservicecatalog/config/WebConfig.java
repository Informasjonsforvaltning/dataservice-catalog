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
                .andRoute(POST("/catalogs/{catalogId}/dataservices"), dataServiceRegistrationHandler::create)
                .andRoute(GET("/catalogs/{catalogId}/dataservices/{dataServiceId}"), dataServiceRegistrationHandler::get)
                .andRoute(PATCH("/catalogs/{catalogId}/dataservices/{dataServiceId}"), dataServiceRegistrationHandler::patch)
                .andRoute(DELETE("/catalogs/{catalogId}/dataservices/{dataServiceId}"), dataServiceRegistrationHandler::delete)

                .andRoute(POST("/catalogs/{catalogId}/dataservices/import"), dataServiceRegistrationHandler::importByUrl)
                .andRoute(POST("/catalogs/{catalogId}/dataservices/{dataServiceId}/import"), dataServiceRegistrationHandler::editByUrl);
    }
}
