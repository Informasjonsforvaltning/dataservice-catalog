package no.fdk.dataservicecatalog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    Flux<DataService> getAllDataservices(@PathVariable("catalogId") String catalogId) {
        return dataServiceRegistrationService.getAllDataservices();
    }

    @PostMapping
    Mono<DataService> createDataservice(@PathVariable("catalogId") String catalogId, @RequestBody @Valid DataService dataService) {
        return dataServiceRegistrationService.create(dataService);
    }

    @GetMapping("/{id}")
    Mono<DataService> getDataservice(@PathVariable("catalogId") String catalogId, @PathVariable("id") String id) {
        return dataServiceRegistrationService.findById(id);
    }

    @PutMapping("/{id}")
    Mono<DataService> patchDataService(
            @PathVariable("catalogId") String catalogId,
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> patchDataService
    ) {
        return dataServiceRegistrationService.patch(patchDataService);
    }

}
