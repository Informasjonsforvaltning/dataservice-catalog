package no.fdk.dataservicecatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableMongoAuditing
@ConfigurationPropertiesScan
@EnableWebFlux
@EnableWebFluxSecurity
public class DataServiceCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataServiceCatalogApplication.class, args);
    }

}