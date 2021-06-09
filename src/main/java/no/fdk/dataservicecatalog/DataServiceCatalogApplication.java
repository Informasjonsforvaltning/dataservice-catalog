package no.fdk.dataservicecatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableMongoAuditing
@ConfigurationPropertiesScan
public class DataServiceCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataServiceCatalogApplication.class, args);
    }

}