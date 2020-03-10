package no.fdk.dataservicecatalog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecificationSource;
import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.service.DataServiceRegistrationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/catalogs/{catalogId}/dataservices", produces = MediaType.APPLICATION_JSON_VALUE)
public class DataServiceRegistrationController {

    private final DataServiceRegistrationService dataServiceRegistrationService;

    @GetMapping
    Flux<DataService> getAllDataServices(@PathVariable("catalogId") String catalogId) {
        return dataServiceRegistrationService.getAllDataServices();
    }

    @PostMapping
    Mono<DataService> createDataService(@PathVariable("catalogId") String catalogId, @RequestBody @Valid DataService dataService) {
        return dataServiceRegistrationService.create(dataService);
    }

    @PostMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    Mono<DataService> importDataService(
            @PathVariable("catalogId") String catalogId,
            @RequestBody @Valid ApiSpecificationSource source
    ) {
        return dataServiceRegistrationService.importFromSpecification(source);
    }

    @GetMapping("/{dataServiceId}")
    Mono<DataService> getDataService(@PathVariable("catalogId") String catalogId, @PathVariable("dataServiceId") String dataServiceId) {
        return dataServiceRegistrationService.findById(dataServiceId);
    }

    @PutMapping("/{dataServiceId}")
    Mono<DataService> patchDataService(
            @PathVariable("catalogId") String catalogId,
            @PathVariable("dataServiceId") String dataServiceId,
            @RequestBody Map<String, Object> patchDataService
    ) {
        return dataServiceRegistrationService.patch(patchDataService);
    }

    @PostMapping(value = "{dataServiceId}/import", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    Mono<DataService> importDataService(
            @PathVariable("catalogId") String catalogId,
            @PathVariable("dataServiceId") String dataServiceId,
            @RequestBody @Valid ApiSpecificationSource source
    ) {
        return dataServiceRegistrationService.importFromSpecification(dataServiceId, source);
    }

}
