package no.fdk.dataservicecatalog.service;

import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.model.Status;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.OutboundMessageResult;
import reactor.rabbitmq.Sender;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DataServiceServiceTest {
    private static final String CATALOG_ID = "12345";

    @Autowired
    DataServiceService dataServiceService;

    @MockBean
    DataServiceMongoRepository dataServiceMongoRepository;

    @MockBean
    Sender sender;

    @Test
    void mustNotTriggerHarvestOnCreateWhenStatusIsDraft() {
        final DataService dataService = DataService.builder()
                .id("MY_FIRST_DATASERVICE")
                .organizationId(CATALOG_ID)
                .status(Status.DRAFT)
                .build();

        when(dataServiceMongoRepository.save(dataService)).thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findAllByOrganizationIdOrderByCreatedDesc(CATALOG_ID))
                .thenReturn(Flux.just(dataService, DataService.builder().build()));
        when(sender.sendWithPublishConfirms(any())).thenReturn(Flux.just(new OutboundMessageResult<>(
                new OutboundMessage("", "", "".getBytes(StandardCharsets.UTF_8)), true)));

        dataServiceService.create(dataService, CATALOG_ID).subscribe();

        verify(sender, times(0)).sendWithPublishConfirms(any());
    }

    @Test
    void mustNotTriggerHarvestOnUpdateWhenStatusIsDraft() {
        final DataService dataService = DataService.builder()
                .id("MY_FIRST_DATASERVICE")
                .organizationId(CATALOG_ID)
                .status(Status.DRAFT)
                .build();

        when(dataServiceMongoRepository.save(dataService)).thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findByIdAndOrganizationId(dataService.getId(), dataService.getOrganizationId()))
                .thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findAllByOrganizationIdOrderByCreatedDesc(CATALOG_ID))
                .thenReturn(Flux.just(dataService, DataService.builder().build()));
        when(sender.sendWithPublishConfirms(any())).thenReturn(Flux.just(new OutboundMessageResult<>(
                new OutboundMessage("", "", "".getBytes(StandardCharsets.UTF_8)), true)));

        dataServiceService.update(dataService.getId(), CATALOG_ID, dataService).subscribe();

        verify(sender, times(0)).sendWithPublishConfirms(any());
    }

    @Test
    void mustTriggerHarvestOnCreateAndNoNewDataSourceWhenHavingMultipleDataservices() {
        final DataService dataService = DataService.builder()
                .id("MY_FIRST_DATASERVICE")
                .organizationId(CATALOG_ID)
                .status(Status.PUBLISHED)
                .build();

        when(dataServiceMongoRepository.save(dataService)).thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findAllByOrganizationIdOrderByCreatedDesc(CATALOG_ID))
                .thenReturn(Flux.just(dataService, DataService.builder().build()));
        when(sender.sendWithPublishConfirms(any())).thenReturn(Flux.just(new OutboundMessageResult<>(
                new OutboundMessage("", "", "".getBytes(StandardCharsets.UTF_8)), true)));

        dataServiceService.create(dataService, CATALOG_ID).subscribe();

        verify(sender, times(1)).sendWithPublishConfirms(any());
    }

    @Test
    void mustTriggerHarvestOnUpdateAndNoNewDataSourceWhenHavingMultipleDataservices() {
        final DataService dataService = DataService.builder()
                .id("MY_FIRST_DATASERVICE")
                .organizationId(CATALOG_ID)
                .status(Status.PUBLISHED)
                .build();

        when(dataServiceMongoRepository.save(dataService)).thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findByIdAndOrganizationId(dataService.getId(), dataService.getOrganizationId()))
                .thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findAllByOrganizationIdOrderByCreatedDesc(CATALOG_ID))
                .thenReturn(Flux.just(dataService, DataService.builder().build()));
        when(sender.sendWithPublishConfirms(any())).thenReturn(Flux.just(new OutboundMessageResult<>(
                new OutboundMessage("", "", "".getBytes(StandardCharsets.UTF_8)), true)));

        dataServiceService.update(dataService.getId(), CATALOG_ID, dataService).subscribe();

        verify(sender, times(1)).sendWithPublishConfirms(any());
    }

    @Test
    void mustTriggerNewDataSourceOnCreateFirstEntity() {
        final DataService dataService = DataService.builder()
                .id("MY_FIRST_DATASERVICE")
                .organizationId(CATALOG_ID)
                .status(Status.PUBLISHED)
                .build();

        when(dataServiceMongoRepository.save(dataService)).thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findAllByOrganizationIdOrderByCreatedDesc(CATALOG_ID))
                .thenReturn(Flux.just(dataService));
        when(sender.sendWithPublishConfirms(any())).thenReturn(Flux.just(new OutboundMessageResult<>(
                new OutboundMessage("", "", "".getBytes(StandardCharsets.UTF_8)), true)));

        dataServiceService.create(dataService, CATALOG_ID).subscribe();

        verify(sender, times(2)).sendWithPublishConfirms(any());
    }

    @Test
    void mustTriggerNewDataSourceOnUpdateFirstEntity() {
        final DataService dataService = DataService.builder()
                .id("MY_FIRST_DATASERVICE")
                .organizationId(CATALOG_ID)
                .status(Status.PUBLISHED)
                .build();

        when(dataServiceMongoRepository.save(dataService)).thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findByIdAndOrganizationId(dataService.getId(), dataService.getOrganizationId()))
                .thenReturn(Mono.just(dataService));
        when(dataServiceMongoRepository.findAllByOrganizationIdOrderByCreatedDesc(CATALOG_ID))
                .thenReturn(Flux.just(dataService));
        when(sender.sendWithPublishConfirms(any())).thenReturn(Flux.just(new OutboundMessageResult<>(
                new OutboundMessage("", "", "".getBytes(StandardCharsets.UTF_8)), true)));

        dataServiceService.update(dataService.getId(), CATALOG_ID, dataService).subscribe();

        verify(sender, times(2)).sendWithPublishConfirms(any());
    }

}
