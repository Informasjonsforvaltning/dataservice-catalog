package no.fdk.dataservicecatalog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("application.external.api-harvester")
public class ApiHarvesterProperties {

    private String url;

}
