package no.fdk.dataservicecatalog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecificationSource;
import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class DataServiceRegistrationService {

    private final ApiHarvesterReactiveClient apiHarvesterReactiveClient;
    private final DataServiceMongoRepository dataServiceMongoRepository;

    private static Map<String, String> setDefaultLanguageValue(String value) {
        return Collections.singletonMap(DataService.DEFAULT_LANGUAGE, value);
    }

    private DataService parseApiSpecification(ApiSpecification apiSpecification) {
        var apiInfo = apiSpecification.getInfo();
        var paths = apiSpecification.getPaths();

        return DataService.builder()
                .license(apiInfo.getLicense())
                .title(setDefaultLanguageValue(apiInfo.getTitle()))
                .description(setDefaultLanguageValue(apiInfo.getDescription()))
                .version(apiInfo.getVersion())
                .operationCount(paths.size())
                .contact(apiInfo.getContact())
                .build();

    }

    private DataService updateFromSpecification(String dataserviceId, ApiSpecification apiSpecification) {
        DataService dataService = this.parseApiSpecification(apiSpecification);
        dataService.setId(dataserviceId);
        return dataService;
    }

    public Mono<DataService> importFromSpecification(ApiSpecificationSource source) {
        Mono<ApiSpecification> apiSpecification = apiHarvesterReactiveClient.convertApiSpecification(source);
        Mono<DataService> dataServiceMono = apiSpecification.map(this::parseApiSpecification)
                .doOnSuccess(dataService -> log.debug("dataservice {} loaded from specification", dataService.getId()))
                .doOnError(error -> log.error("new dataservice failed mapping: {}", error.getMessage()));
        return dataServiceMono.flatMap(dataServiceMongoRepository::save);

    }

    public Mono<DataService> importFromSpecification(String dataServiceId, ApiSpecificationSource source) {
        Mono<ApiSpecification> apiSpecification = apiHarvesterReactiveClient.convertApiSpecification(source);
        Mono<DataService> dataServiceMono = apiSpecification.map(spec -> this.updateFromSpecification(dataServiceId, spec))
                .doOnSuccess(dataService -> log.debug("dataservice {} loaded from specification", dataService.getId()))
                .doOnError(error -> log.error("dataservice with id {} failed mapping {}", dataServiceId, error.getMessage()));
        return dataServiceMono.flatMap(dataServiceMongoRepository::save);
    }

    public Flux<DataService> getAllDataServices() {
        var all = dataServiceMongoRepository.findAll()
                .doOnError(error -> log.error("error retrieving all dataservices from mongo: {}", error.getMessage()));
        all.count().subscribe(count -> log.debug("found {} dataservices", count));
        return all;
    }

    public Mono<DataService> create(DataService dataService) {
        return dataServiceMongoRepository.save(dataService)
                .doOnSuccess(saved -> log.debug("dataservice {} saved", saved.getId()))
                .doOnError(error -> log.error("error saving dataservice to database: {}", error.getMessage()));
    }

    public Mono<DataService> findById(String id) {
        return dataServiceMongoRepository.findById(id)
                .doOnSuccess(dataService -> {
                    if (dataService != null) {
                        log.debug("dataservice {} retrieved", dataService.getId());
                    } else {
                        log.error("no dataservice exists with id {}", id);
                    }
                })
                .doOnError(error -> log.error("error retrieving dataservice {}: {}", id, error.getMessage()));
    }

    public Mono<DataService> patch(Map<String, Object> partialDataService) {
        return Mono.just(new DataService());
    }
}
