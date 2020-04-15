package no.fdk.dataservicecatalog.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecificationSource;
import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.service.DataServiceService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataServiceRegistrationHandler {

    private final DataServiceService dataServiceService;

    public Mono<ServerResponse> all(ServerRequest serverRequest) {
        return ok().body(dataServiceService.getAllDataServices(serverRequest.pathVariable("catalogId")), DataService.class);
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(DataService.class)
                .flatMap(dataService -> ok().body(
                        dataServiceService.create(dataService, serverRequest.pathVariable("catalogId")), DataService.class)
                );
    }

    public Mono<ServerResponse> get(ServerRequest serverRequest) {
        return dataServiceService.findById(serverRequest.pathVariable("dataServiceId"), serverRequest.pathVariable("catalogId"))
                .flatMap(dataService -> ok().body(Mono.just(dataService), DataService.class))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        var dataServiceId = serverRequest.pathVariable("dataServiceId");
        var catalogId = serverRequest.pathVariable("catalogId");
        return dataServiceService.deleteById(dataServiceId, catalogId)
                .flatMap(isDeleted -> isDeleted ? noContent().build(): notFound().build());
    }

    public Mono<ServerResponse> patch(ServerRequest serverRequest) {
        var dataServiceId = serverRequest.pathVariable("dataServiceId");
        var catalogId = serverRequest.pathVariable("catalogId");
        return serverRequest.bodyToMono(DataService.class)
                .flatMap(updated -> ok().body(dataServiceService.update(dataServiceId, catalogId, updated), DataService.class));
    }

    public Mono<ServerResponse> importByUrl(ServerRequest serverRequest) {
        var catalogId = serverRequest.pathVariable("catalogId");
        return serverRequest.bodyToMono(ApiSpecificationSource.class)
                .flatMap(apiSpecificationSource -> ok().body(dataServiceService.importFromSpecification(apiSpecificationSource, catalogId), DataService.class));
    }

    public Mono<ServerResponse> editByUrl(ServerRequest serverRequest) {
        var dataServiceId = serverRequest.pathVariable("dataServiceId");
        var catalogId = serverRequest.pathVariable("catalogId");
        return serverRequest.bodyToMono(ApiSpecificationSource.class)
                .flatMap(source -> ok().body(dataServiceService.importFromSpecification(dataServiceId, catalogId, source), DataService.class));
    }
}
