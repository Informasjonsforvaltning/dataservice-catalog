package no.fdk.dataservicecatalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.fdk.dataservicecatalog.config.ApplicationProperties;
import no.fdk.dataservicecatalog.dto.shared.apispecification.servers.Server;
import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.model.Status;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import no.fdk.dataservicecatalog.utils.TestData;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.OutboundMessageResult;
import reactor.rabbitmq.Sender;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DataServiceServiceTest {
    private static final String CATALOG_ID = "12345";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter objectWriter = objectMapper.writer();

    @Autowired
    DataServiceService dataServiceService;

    @MockBean
    DataServiceMongoRepository dataServiceMongoRepository;

    @MockBean
    Sender sender;

    @Test
    void mustTriggerHarvestOnSaveAndNoNewDataSourceWhenHavingMultipleDataservices() {
        final DataService dataService = DataService.builder()
                .id("MY_FIRST_DATASERVICE")
                .organizationId(CATALOG_ID)
                .status(Status.PUBLISHED)
                .build();

        when(dataServiceMongoRepository.save(dataService)).thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findAllByOrganizationIdOrderByCreatedDesc(CATALOG_ID))
                .thenReturn(Flux.just(dataService, DataService.builder().build()));
        when(sender.sendWithPublishConfirms(any())).thenReturn(Flux.just(new OutboundMessageResult<OutboundMessage>(
                new OutboundMessage("", "", "".getBytes(StandardCharsets.UTF_8)), true)));

        dataServiceService.create(dataService, CATALOG_ID).subscribe();

        verify(sender, times(1)).sendWithPublishConfirms(any());
    }

    @Test
    void mustTriggerNewDataSourceOnFirstEntity() {
        final DataService dataService = DataService.builder()
                .id("MY_FIRST_DATASERVICE")
                .organizationId(CATALOG_ID)
                .status(Status.PUBLISHED)
                .build();

        when(dataServiceMongoRepository.save(dataService)).thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findAllByOrganizationIdOrderByCreatedDesc(CATALOG_ID))
                .thenReturn(Flux.just(dataService));
        when(sender.sendWithPublishConfirms(any())).thenReturn(Flux.just(new OutboundMessageResult<OutboundMessage>(
                new OutboundMessage("", "", "".getBytes(StandardCharsets.UTF_8)), true)));

        dataServiceService.create(dataService, CATALOG_ID).subscribe();

        verify(sender, times(2)).sendWithPublishConfirms(any());
    }

}
