package no.fdk.dataservicecatalog.repository;

import no.fdk.dataservicecatalog.model.DataService;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataServiceMongoRepository extends ReactiveMongoRepository<DataService, String> {
    Mono<DataService> findByIdAndCatalogId(String dataServiceId, String catalogId);
    Mono<Long> deleteByIdAndCatalogId(String dataServiceId, String catalogId);
    Flux<DataService> findAllByCatalogId(String catalogId);
}
