package no.fdk.dataservicecatalog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.service.DcatApNoModelService;
import org.apache.jena.riot.Lang;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogHandler {
    private final DcatApNoModelService dcatApNoModelService;

    public Mono<ServerResponse> listCatalogs(ServerRequest serverRequest) {
        Lang jenaLang = dcatApNoModelService.jenaLangFromAcceptHeader(serverRequest.headers().accept());
        log.info("Starting to build catalogs model");
        return dcatApNoModelService
                .buildCatalogsModel()
                .doOnSuccess(model -> log.info("Successfully built catalogs model"))
                .doOnError(error -> log.error("Failed to build catalogs model", error))
                .flatMap(model -> ok().bodyValue(dcatApNoModelService.serialise(model, jenaLang)));
    }

    public Mono<ServerResponse> getCatalog(ServerRequest serverRequest) {
        String catalogId = serverRequest.pathVariable("catalogId");
        Lang jenaLang = dcatApNoModelService.jenaLangFromAcceptHeader(serverRequest.headers().accept());
        log.info("Starting to build catalog model for catalog with ID {}", catalogId);
        return dcatApNoModelService
                .buildCatalogModel(catalogId)
                .doOnSuccess(model -> log.info("Successfully built catalog model for catalog with ID {}", catalogId))
                .doOnError(error -> log.info("Failed to build catalog model for catalog with ID {}", catalogId, error))
                .flatMap(model -> ok().bodyValue(dcatApNoModelService.serialise(model, jenaLang)));
    }

    public Mono<ServerResponse> getDataService(ServerRequest serverRequest) {
        String dataServiceId = serverRequest.pathVariable("dataServiceId");
        Lang jenaLang = dcatApNoModelService.jenaLangFromAcceptHeader(serverRequest.headers().accept());
        log.info("Starting to build model for data service with ID {}", dataServiceId);
        return dcatApNoModelService
                .buildDataServiceModel(dataServiceId)
                .doOnSuccess(model -> log.info("Successfully built model for data service with ID {}", dataServiceId))
                .doOnError(error -> log.info("Failed to build model for data service with ID {}", dataServiceId, error))
                .flatMap(model -> ok().bodyValue(dcatApNoModelService.serialise(model, jenaLang)));
    }
}
