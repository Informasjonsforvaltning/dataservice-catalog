package no.fdk.dataservicecatalog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class DataServiceRegistrationService {

    private final DataServiceMongoRepository dataServiceMongoRepository;

    public Flux<DataService> getAllDataservices() {
        return dataServiceMongoRepository.findAll();
    }

    public Mono<DataService> create(DataService dataService) {
        return dataServiceMongoRepository.save(dataService);
    }

    public Mono<DataService> findById(String id) {
        return dataServiceMongoRepository.findById(id);
    }

    public Mono<DataService> patch(Map<String, Object> patchDataService) {
        return Mono.just(new DataService());
    }
}
