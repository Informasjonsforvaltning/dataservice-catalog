package no.fdk.dataservicecatalog.service;

import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import no.fdk.dataservicecatalog.utils.TestData;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;

import java.io.StringReader;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DcatApNoModelServiceTest {
    @Autowired
    DcatApNoModelService dcatApNoModelService;

    @MockBean
    DataServiceMongoRepository dataServiceMongoRepository;

    @Test
    void mustCorrectlyBuildCatalogsModelWhenCatalogsDoNotExist() {
        Flux<DataService> dataServices = Flux.just();

        when(dataServiceMongoRepository.findAll()).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogsModel().block();
        Model expectedModel = ModelFactory.createDefaultModel();

        assertNotNull(model);
        assertNotNull(expectedModel);
        assertTrue(model.isIsomorphicWith(expectedModel));
    }

    @Test
    void mustCorrectlyBuildCatalogsModelWhenCatalogsExist() {
        Flux<DataService> dataServices = Flux.merge(
                Flux.fromIterable(TestData.createDataServices("catalog-id-1")),
                Flux.fromIterable(TestData.createDataServices("catalog-id-2"))
        );

        when(dataServiceMongoRepository.findAll()).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogsModel().block();
        Model expectedModel = RDFDataMgr.loadModel("catalogs.ttl");

        assertNotNull(model);
        assertNotNull(expectedModel);
        assertTrue(model.isIsomorphicWith(expectedModel));
    }

    @Test
    void mustCorrectlyBuildCatalogModelWhenCatalogDoesNotExist() {
        String catalogId = "catalog-id-1";
        Flux<DataService> dataServices = Flux.just();

        when(dataServiceMongoRepository.findAllByCatalogId(catalogId)).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogModel(catalogId).block();
        Model expectedModel = ModelFactory.createDefaultModel();

        assertNotNull(model);
        assertNotNull(expectedModel);
        assertTrue(model.isIsomorphicWith(expectedModel));
    }

    @Test
    void mustCorrectlyBuildCatalogModelWhenCatalogExists() {
        String catalogId = "catalog-id-1";
        Flux<DataService> dataServices = Flux.fromIterable(TestData.createDataServices(catalogId));

        when(dataServiceMongoRepository.findAllByCatalogId(catalogId)).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogModel(catalogId).block();
        Model expectedModel = RDFDataMgr.loadModel("catalog.ttl");

        assertNotNull(model);
        assertNotNull(expectedModel);
        assertTrue(model.isIsomorphicWith(expectedModel));
    }

    @Test
    void mustCorrectlySerialiseCatalogsModelAsTextTurtle() {
        Flux<DataService> dataServices = Flux.merge(
                Flux.fromIterable(TestData.createDataServices("catalog-id-1")),
                Flux.fromIterable(TestData.createDataServices("catalog-id-2"))
        );

        when(dataServiceMongoRepository.findAll()).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogsModel().block();
        String serialisedModel = dcatApNoModelService.serialise(Objects.requireNonNull(model));

        Model deserialisedModel = ModelFactory.createDefaultModel().read(new StringReader(serialisedModel), null, "TURTLE");
        Model expectedModel = RDFDataMgr.loadModel("catalogs.ttl");

        assertNotNull(deserialisedModel);
        assertNotNull(expectedModel);
        assertTrue(deserialisedModel.isIsomorphicWith(expectedModel));
    }

    @Test
    void mustCorrectlySerialiseCatalogModelAsTextTurtle() {
        String catalogId = "catalog-id-1";
        Flux<DataService> dataServices = Flux.fromIterable(TestData.createDataServices(catalogId));

        when(dataServiceMongoRepository.findAllByCatalogId(catalogId)).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogModel(catalogId).block();
        String serialisedModel = dcatApNoModelService.serialise(Objects.requireNonNull(model));

        Model deserialisedModel = ModelFactory.createDefaultModel().read(new StringReader(serialisedModel), null, "TURTLE");
        Model expectedModel = RDFDataMgr.loadModel("catalog.ttl");

        assertNotNull(deserialisedModel);
        assertNotNull(expectedModel);
        assertTrue(deserialisedModel.isIsomorphicWith(expectedModel));
    }
}
