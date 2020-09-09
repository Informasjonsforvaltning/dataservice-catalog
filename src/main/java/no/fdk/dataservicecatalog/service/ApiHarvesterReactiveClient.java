package no.fdk.dataservicecatalog.service;

import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.config.ApiHarvesterProperties;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecificationSource;
import no.fdk.dataservicecatalog.exceptions.ParseException;
import no.fdk.dataservicecatalog.service.parser.UniversalParser;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ApiHarvesterReactiveClient {

    private final WebClient webClient;

    ApiHarvesterReactiveClient(ApiHarvesterProperties properties) {
        webClient = WebClient.builder().defaultHeader("accept", MediaType.APPLICATION_JSON_VALUE).build();
    }

    Mono<ApiSpecification> convertApiSpecification(ApiSpecificationSource source) {
        var url = source.getApiSpecUrl();
        return webClient.get().uri(url).retrieve()
                .bodyToMono(String.class).map(spec -> {
                    try {
                        return new UniversalParser().parse(spec);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return new ApiSpecification();
                });
    }

}
