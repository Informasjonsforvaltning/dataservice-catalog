package no.fdk.dataservicecatalog.controller;

import lombok.RequiredArgsConstructor;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@RequiredArgsConstructor
public class ApplicationStatusHandler {
    private final DataServiceMongoRepository mongoRepository;

    public Mono<ServerResponse> ping(ServerRequest serverRequest) {
        return ok().bodyValue("OK");
    }

    public Mono<ServerResponse> ready(ServerRequest serverRequest) {
        return mongoRepository.count()
                .flatMap(count -> ok().bodyValue("OK"))
                .onErrorMap(error -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE));
    }
}
