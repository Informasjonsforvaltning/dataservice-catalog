package no.fdk.dataservicecatalog.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.config.ApiHarvesterProperties;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiHarvesterResponse;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecificationSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ApiHarvesterReactiveClient {

    private final WebClient webClient;

    ApiHarvesterReactiveClient(ApiHarvesterProperties properties) {
        webClient = WebClient.builder().defaultHeader("accept", MediaType.APPLICATION_JSON_VALUE).baseUrl(properties.getUrl()).build();
    }

    Mono<ApiSpecification> convertApiSpecification(ApiSpecificationSource source) {
        var payload = JsonNodeFactory.instance.objectNode().put("url", source.getApiSpecUrl());

        return webClient.post()
                .uri("/apis/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(ApiHarvesterResponse.class)
                .map(ApiHarvesterResponse::getApiSpecification);
    }

}
