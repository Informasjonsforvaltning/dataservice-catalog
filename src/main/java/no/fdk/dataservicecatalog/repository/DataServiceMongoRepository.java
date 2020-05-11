package no.fdk.dataservicecatalog.repository;

import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.model.Status;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataServiceMongoRepository extends ReactiveMongoRepository<DataService, String> {
    Mono<DataService> findByIdAndOrganizationId(String dataServiceId, String organizationId);
    Mono<Long> deleteByIdAndOrganizationId(String dataServiceId, String organizationId);
    Flux<DataService> findAllByOrganizationIdOrderByCreatedDesc(String organizationId);
    Flux<DataService> findAllByStatus(Status Status);
    Flux<DataService> findAllByOrganizationIdAndStatus(String organizationId, Status status);
}
