package no.fdk.dataservicecatalog.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecificationSource;
import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.service.DataServiceService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

import static java.lang.String.format;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

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
                .flatMap(dataService -> dataServiceService.create(dataService, serverRequest.pathVariable("catalogId")))
                .flatMap(c -> created(URI.create(format("%s/%s", serverRequest.path(), c.getId()))).build());
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
                .flatMap(deleted -> {
                    var isDeleted = JsonNodeFactory.instance.objectNode().put("success", deleted);
                    return ok().body(Mono.just(isDeleted), ObjectNode.class);
                });
    }

    public Mono<ServerResponse> patch(ServerRequest serverRequest) {
        var dataServiceId = serverRequest.pathVariable("dataServiceId");
        var catalogId = serverRequest.pathVariable("catalogId");
        return serverRequest.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .flatMap(fields -> ok().body(dataServiceService.patch(dataServiceId, catalogId, fields), DataService.class));
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
