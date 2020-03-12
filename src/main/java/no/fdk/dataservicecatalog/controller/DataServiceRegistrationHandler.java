package no.fdk.dataservicecatalog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecificationSource;
import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.service.DataServiceRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static java.lang.String.format;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataServiceRegistrationHandler {

    private final DataServiceRegistrationService dataServiceRegistrationService;

    public Mono<ServerResponse> all(ServerRequest serverRequest) {
        return ok().body(dataServiceRegistrationService.getAllDataServices(), DataService.class);
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(DataService.class)
                .flatMap(dataServiceRegistrationService::create)
                .flatMap(c -> created(URI.create(format("/dataservices/%s", c.getId()))).build());
    }

    public Mono<ServerResponse> get(ServerRequest serverRequest) {
        return dataServiceRegistrationService.findById(serverRequest.pathVariable("dataServiceId"))
                .flatMap(dataService -> ok().body(Mono.just(dataService), DataService.class))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> patch(ServerRequest serverRequest) {
        return status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    public Mono<ServerResponse> importByUrl(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ApiSpecificationSource.class)
                .flatMap(dataServiceRegistrationService::importFromSpecification)
                .flatMap(dataService -> ok().body(Mono.just(dataService), DataService.class));
    }

    public Mono<ServerResponse> editByUrl(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ApiSpecificationSource.class)
                .flatMap(source -> dataServiceRegistrationService.importFromSpecification(serverRequest.pathVariable("dataServiceId"), source))
                .flatMap(dataService -> ok().body(Mono.just(dataService), DataService.class));
    }
}
