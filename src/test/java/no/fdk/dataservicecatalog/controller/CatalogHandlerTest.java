package no.fdk.dataservicecatalog.controller;

import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import no.fdk.dataservicecatalog.utils.TestData;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.io.StringReader;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
public class CatalogHandlerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DataServiceMongoRepository dataServiceMongoRepository;

    @Test
    void mustCorrectlyListCatalogsInRdfFormatWhenCatalogsDoNotExist() {
        Flux<DataService> dataServices = Flux.just();

        when(dataServiceMongoRepository.findAll()).thenReturn(dataServices);

        Model expectedModel = ModelFactory.createDefaultModel();

        webTestClient
                .get()
                .uri("/catalogs")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(response -> {
                    Model model = ModelFactory.createDefaultModel().read(new StringReader(new String(Objects.requireNonNull(response.getResponseBody()))), null, "TURTLE");

                    assertNotNull(model);
                    assertNotNull(expectedModel);
                    assertTrue(model.isIsomorphicWith(expectedModel));
                });
    }

    @Test
    void mustCorrectlyListCatalogsInRdfFormatWhenCatalogsExist() {
        Flux<DataService> dataServices = Flux.merge(
                Flux.fromIterable(TestData.createDataServices("catalog-id-1")),
                Flux.fromIterable(TestData.createDataServices("catalog-id-2"))
        );

        when(dataServiceMongoRepository.findAll()).thenReturn(dataServices);

        Model expectedModel = RDFDataMgr.loadModel("catalogs.ttl");

        webTestClient
                .get()
                .uri("/catalogs")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(response -> {
                    Model model = ModelFactory.createDefaultModel().read(new StringReader(new String(Objects.requireNonNull(response.getResponseBody()))), null, "TURTLE");

                    assertNotNull(model);
                    assertNotNull(expectedModel);
                    assertTrue(model.isIsomorphicWith(expectedModel));
                });
    }

    @Test
    void mustCorrectlyListCatalogsInRdfFormatWhenCatalogDoesNotExist() {
        String catalogId = "catalog-id-1";
        Flux<DataService> dataServices = Flux.just();

        when(dataServiceMongoRepository.findAllByOrganizationId(catalogId)).thenReturn(dataServices);

        Model expectedModel = ModelFactory.createDefaultModel();

        webTestClient
                .get()
                .uri(String.format("/catalogs/%s", catalogId))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(response -> {
                    Model model = ModelFactory.createDefaultModel().read(new StringReader(new String(Objects.requireNonNull(response.getResponseBody()))), null, "TURTLE");

                    assertNotNull(model);
                    assertNotNull(expectedModel);
                    assertTrue(model.isIsomorphicWith(expectedModel));
                });
    }

    @Test
    void mustCorrectlyListCatalogsInRdfFormatWhenCatalogExists() {
        String catalogId = "catalog-id-1";
        Flux<DataService> dataServices = Flux.fromIterable(TestData.createDataServices(catalogId));

        when(dataServiceMongoRepository.findAllByOrganizationId(catalogId)).thenReturn(dataServices);

        Model expectedModel = RDFDataMgr.loadModel("catalog.ttl");

        webTestClient
                .get()
                .uri(String.format("/catalogs/%s", catalogId))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(response -> {
                    Model model = ModelFactory.createDefaultModel().read(new StringReader(new String(Objects.requireNonNull(response.getResponseBody()))), null, "TURTLE");

                    assertNotNull(model);
                    assertNotNull(expectedModel);
                    assertTrue(model.isIsomorphicWith(expectedModel));
                });
    }
}
