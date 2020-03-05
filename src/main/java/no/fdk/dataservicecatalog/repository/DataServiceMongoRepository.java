package no.fdk.dataservicecatalog.repository;

import no.fdk.dataservicecatalog.model.DataService;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DataServiceMongoRepository extends ReactiveMongoRepository<DataService, String> {

}
