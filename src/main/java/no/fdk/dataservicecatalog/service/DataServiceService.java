package no.fdk.dataservicecatalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.config.ApplicationProperties;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecificationSource;
import no.fdk.dataservicecatalog.dto.shared.apispecification.info.Info;
import no.fdk.dataservicecatalog.dto.shared.apispecification.servers.Server;
import no.fdk.dataservicecatalog.exceptions.NotFoundException;
import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.model.Status;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DataServiceService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper.writer();

    private final Sender sender;
    private final ApiHarvesterReactiveClient apiHarvesterReactiveClient;
    private final DataServiceMongoRepository dataServiceMongoRepository;
    private final ApplicationProperties applicationProperties;
    private final RabbitProperties rabbitProperties;

    private static Map<String, String> setDefaultLanguageValue(String value) {
        return Collections.singletonMap(DataService.DEFAULT_LANGUAGE, value);
    }

    private DataService parseApiSpecification(ApiSpecification apiSpecification, ApiSpecificationSource source, String organizationId, String dataServiceId) {
        var apiInfo = apiSpecification.getInfo();
        if (apiInfo == null) {
            apiInfo = new Info();
        }

        var paths = apiSpecification.getPaths();
        if (paths == null) {
            paths = Collections.emptyMap();
        }

        var servers = apiSpecification.getServers();
        if (servers == null) {
            servers = Collections.emptyList();
        }

        return DataService.builder()
                .id(dataServiceId)
                .organizationId(organizationId)
                .license(apiInfo.getLicense())
                .title(setDefaultLanguageValue(apiInfo.getTitle()))
                .description(setDefaultLanguageValue(apiInfo.getDescription()))
                .version(apiInfo.getVersion())
                .operationCount(paths.size())
                .contact(apiInfo.getContact())
                .mediaTypes(apiSpecification.getFormats())
                .endpointUrls(servers.stream().map(Server::getUrl).collect(Collectors.toList()))
                .externalDocs(apiSpecification.getExternalDocs())
                .endpointDescriptions(List.of(source.getApiSpecUrl()))
                .termsOfServiceUrl(apiInfo.getTermsOfService())
                .status(Status.DRAFT)
                .imported(true)
                .build();

    }

    public Mono<DataService> importFromSpecification(ApiSpecificationSource source, String catalogId) {
        Mono<ApiSpecification> apiSpecification = apiHarvesterReactiveClient.convertApiSpecification(source);
        Mono<DataService> dataServiceMono = apiSpecification.map(apiSpecification1 -> parseApiSpecification(apiSpecification1, source, catalogId, null))
                .doOnSuccess(dataService -> log.debug("dataservice loaded from specification"))
                .doOnError(error -> log.error("new dataservice failed mapping", error));
        return dataServiceMono.flatMap(dataServiceMongoRepository::save);
    }

    public Mono<DataService> importFromSpecification(String dataServiceId, String catalogId, ApiSpecificationSource source) {
        Mono<ApiSpecification> apiSpecification = apiHarvesterReactiveClient.convertApiSpecification(source);
        Mono<DataService> dataServiceMono = apiSpecification.map(apiSpecification1 -> parseApiSpecification(apiSpecification1, source, catalogId, dataServiceId))
                .doOnSuccess(dataService -> log.debug("dataservice {} loaded from specification", dataService.getId()))
                .doOnError(error -> log.error("dataservice with id {} failed mapping", dataServiceId, error));
        return dataServiceMono.flatMap(dataServiceMongoRepository::save);
    }

    public Flux<DataService> getAllDataServices(String catalogId) {
        var all = dataServiceMongoRepository.findAllByOrganizationIdOrderByCreatedDesc(catalogId)
                .doOnError(error -> log.error("error retrieving all dataservices from mongo", error));
        all.count().subscribe(count -> log.debug("found {} dataservices", count));
        return all;
    }

    private OutboundMessage getOutboundMessage(ObjectNode payload) {
      return getOutboundMessage(payload, null);
    }

    private OutboundMessage getOutboundMessage(ObjectNode payload, final String routingKey) {
        try {
            final byte[] message = objectWriter.writeValueAsBytes(payload);
            final var rabbitTemplate = rabbitProperties.getTemplate();
            return new OutboundMessage(
                    rabbitTemplate.getExchange(),
                    routingKey != null ? routingKey : rabbitTemplate.getRoutingKey(),
                    message
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void triggerHarvest(final DataService dataService) {
        try {
            log.debug("Trigger harvest for dataservice {}", dataService.getId());
            sender.sendWithPublishConfirms(Flux
                    .just(objectMapper.createObjectNode())
                    .map(payload -> {
                        payload.put("publisherId", dataService.getOrganizationId());
                        payload.put("dataType", "dataservice");
                        return getOutboundMessage(payload);
                    }))
                    .onErrorResume(Mono::error)
                    .subscribe(result -> log.debug(result.toString()));
        } catch(RuntimeException e) {
            log.error("Unable to trigger dataservice harvest", e);
        }
    }

    private void createNewDataSource(final DataService dataService, final String harvestUrl) {
        try {
            log.debug("Create new data source for dataservice {}", dataService.getId());
            sender.sendWithPublishConfirms(Flux
                    .just(objectMapper.createObjectNode())
                    .map(payload -> {
                        payload.put("publisherId", dataService.getOrganizationId());
                        payload.put("url", harvestUrl);
                        payload.put("dataType", "dataservice");
                        payload.put("dataSourceType", "DCAT-AP-NO");
                        payload.put("acceptHeaderValue", "text/turtle");
                        payload.put("description",
                                String.format("Automatically generated data source for %s",
                                        dataService.getOrganizationId()));

                        return getOutboundMessage(payload, "dataservice.publisher.NewDataSource");
                    }))
                    .onErrorResume(Mono::error)
                    .subscribe(result -> log.debug(result.toString()));
        } catch(RuntimeException e) {
            log.error("Unable to create new datasource", e);
        }
    }

    public Mono<DataService> create(DataService dataService, String catalogId) {
        dataService.setCreated(LocalDateTime.now());
        dataService.setOrganizationId(catalogId);
        if (dataService.getStatus() == null || !List.of(Status.DRAFT, Status.PUBLISHED).contains(dataService.getStatus())) {
            dataService.setStatus(Status.DRAFT);
        }

        return dataServiceMongoRepository.save(dataService)
                .doOnSuccess(saved -> {
                    log.debug("dataservice {} saved", saved.getId());
                    if (saved.getStatus() == Status.PUBLISHED) {
                        triggerHarvest(saved);

                        dataServiceMongoRepository.findAllByOrganizationIdAndStatus(catalogId, Status.PUBLISHED)
                            .count()
                            .doOnNext(count -> log.debug("total count of published dataservices for catalog {}: {}",
                                    catalogId, count))
                            .filter(count -> count == 1)
                            .doOnNext(count -> createNewDataSource(saved, String.format("%s/%s",
                                    applicationProperties.getCatalogBaseUri(),
                                    catalogId)))
                            .subscribe();
                    }
                })
                .doOnError(error -> log.error("error saving dataservice to database", error));
    }

    public Mono<DataService> findById(String dataServiceId, String catalogId) {
        return dataServiceMongoRepository.findByIdAndOrganizationId(dataServiceId, catalogId)
                .doOnSuccess(dataService -> {
                    if (dataService != null) {
                        log.debug("dataservice {} retrieved", dataService.getId());
                    } else {
                        log.error("no dataservice with id {} exists for catalog {}", dataServiceId, catalogId, new NotFoundException());
                    }
                })
                .doOnError(error -> log.error("error retrieving dataservice {}", dataServiceId, error));
    }

    public Mono<Boolean> deleteById(String dataServiceId, String catalogId) {
        return dataServiceMongoRepository.deleteByIdAndOrganizationId(dataServiceId, catalogId)
                .doOnError(error -> log.error("error deleting dataservice {}", dataServiceId, error))
                .map(deletedCount -> deletedCount > 0)
                .doOnSuccess(deleted -> log.debug("dataset {} deleted: {}", dataServiceId, deleted));
    }

    public Mono<DataService> update(String dataServiceId, String catalogId, DataService updated) {
        return dataServiceMongoRepository.findByIdAndOrganizationId(dataServiceId, catalogId)
                .doOnError(error -> log.error("error retrieving dataservice {}", dataServiceId, error))
                .flatMap(dataService -> {
                    if (dataService != null) {
                        log.debug("dataservice {} retrieved for patch", dataService.getId());
                        updated.setId(dataServiceId);
                        updated.setCreated(dataService.getCreated());
                        updated.setModified(LocalDateTime.now());
                        return dataServiceMongoRepository.save(updated).doOnSuccess(saved -> {
                            var updatedStatus = updated.getStatus();
                            if (updatedStatus == Status.PUBLISHED || dataService.getStatus() != updatedStatus) {
                                triggerHarvest(saved);

                                dataServiceMongoRepository.findAllByOrganizationIdAndStatus(catalogId, Status.PUBLISHED)
                                        .count()
                                        .doOnNext(count -> log.debug("total count of published dataservices for catalog {}: {}",
                                                catalogId, count))
                                        .filter(count -> count == 1)
                                        .doOnNext(count -> createNewDataSource(saved, String.format("%s/catalogs/%s",
                                                applicationProperties.getCatalogBaseUri(),
                                                catalogId)))
                                        .subscribe();
                            }
                        });
                    }
                    log.error("no dataservice with id {} exists for catalog {}", dataServiceId, catalogId, new NotFoundException());
                    return Mono.error(new NotFoundException("no dataservice found"));
                });
    }
}
