package no.fdk.dataservicecatalog;

import no.fdk.dataservicecatalog.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveMongoRepositories
@EnableMongoAuditing
@EnableConfigurationProperties({ApplicationProperties.class})
public class DataServiceCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataServiceCatalogApplication.class, args);
    }

}