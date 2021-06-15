package no.fdk.dataservicecatalog.config;

import no.fdk.dataservicecatalog.controller.CatalogHandler;
import no.fdk.dataservicecatalog.controller.DataServiceRegistrationHandler;
import org.apache.jena.riot.Lang;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Bean
    public RouterFunction<ServerResponse> catalogRouter(CatalogHandler catalogHandler) {
        return route(GET("/catalogs").and(rdfAccept()), catalogHandler::listCatalogs)
                .andRoute(GET("/catalogs/{catalogId}").and(rdfAccept()), catalogHandler::getCatalog)
                .andRoute(GET("/catalogs/{catalogId}/dataservices/{dataServiceId}").and(rdfAccept()), catalogHandler::getDataService);
    }

    @Bean
    public RouterFunction<ServerResponse> dataServiceRegistrationRouter(DataServiceRegistrationHandler dataServiceRegistrationHandler) {
        return route(GET("/catalogs/{catalogId}/dataservices"), dataServiceRegistrationHandler::all)
                .andRoute(PATCH("/catalogs/{catalogId}/dataservices"), dataServiceRegistrationHandler::create)
                .andRoute(GET("/catalogs/{catalogId}/dataservices/{dataServiceId}"), dataServiceRegistrationHandler::get)
                .andRoute(PATCH("/catalogs/{catalogId}/dataservices/{dataServiceId}"), dataServiceRegistrationHandler::patch)
                .andRoute(DELETE("/catalogs/{catalogId}/dataservices/{dataServiceId}"), dataServiceRegistrationHandler::delete)

                .andRoute(POST("/catalogs/{catalogId}/dataservices"), dataServiceRegistrationHandler::importByUrl)
                .andRoute(POST("/catalogs/{catalogId}/dataservices/{dataServiceId}/import"), dataServiceRegistrationHandler::editByUrl);
    }

    private RequestPredicate rdfAccept() {
        return accept(MediaType.valueOf("text/n3"),
                MediaType.valueOf(Lang.TURTLE.getHeaderString()), MediaType.valueOf(Lang.RDFXML.getHeaderString()),
                MediaType.valueOf(Lang.RDFJSON.getHeaderString()), MediaType.valueOf(Lang.JSONLD.getHeaderString()),
                MediaType.valueOf(Lang.TRIX.getHeaderString()), MediaType.valueOf(Lang.TRIG.getHeaderString()),
                MediaType.valueOf(Lang.NQUADS.getHeaderString()), MediaType.valueOf(Lang.NTRIPLES.getHeaderString()));
    }

}
