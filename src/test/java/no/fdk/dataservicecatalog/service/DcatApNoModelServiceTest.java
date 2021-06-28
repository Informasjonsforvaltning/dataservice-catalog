package no.fdk.dataservicecatalog.service;

import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.model.Status;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import no.fdk.dataservicecatalog.utils.TestData;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;

import java.io.StringReader;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
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

        when(dataServiceMongoRepository.findAllByStatus(Status.PUBLISHED)).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogsModel().block();
        Model expectedModel = ModelFactory.createDefaultModel();

        assertNotNull(model);
        assertNotNull(expectedModel);

        boolean isomorphic = model.isIsomorphicWith(expectedModel);
        if(!isomorphic) {
            assertEquals(
                    dcatApNoModelService.serialise(Objects.requireNonNull(expectedModel), Lang.TURTLE),
                    dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.TURTLE));
        }
    }

    @Test
    void mustCorrectlyBuildCatalogsModelWhenCatalogsExist() {
        Flux<DataService> dataServices = Flux.merge(
                Flux.fromIterable(TestData.createDataServices("catalog-id-1")),
                Flux.fromIterable(TestData.createDataServices("catalog-id-2"))
        );

        when(dataServiceMongoRepository.findAllByStatus(Status.PUBLISHED)).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogsModel().block();
        Model expectedModel = RDFDataMgr.loadModel("catalogs.ttl");

        assertNotNull(model);
        assertNotNull(expectedModel);

        boolean isomorphic = model.isIsomorphicWith(expectedModel);
        if(!isomorphic) {
            assertEquals(
                    dcatApNoModelService.serialise(Objects.requireNonNull(expectedModel), Lang.TURTLE),
                    dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.TURTLE));
        }
    }

    @Test
    void mustCorrectlyBuildCatalogModelWhenCatalogDoesNotExist() {
        String catalogId = "catalog-id-1";
        Flux<DataService> dataServices = Flux.just();

        when(dataServiceMongoRepository.findAllByOrganizationIdAndStatus(catalogId, Status.PUBLISHED)).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogModel(catalogId).block();
        Model expectedModel = ModelFactory.createDefaultModel();

        assertNotNull(model);
        assertNotNull(expectedModel);

        boolean isomorphic = model.isIsomorphicWith(expectedModel);
        if(!isomorphic) {
            assertEquals(
                    dcatApNoModelService.serialise(Objects.requireNonNull(expectedModel), Lang.TURTLE),
                    dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.TURTLE));
        }
    }

    @Test
    void mustCorrectlyBuildCatalogModelWhenCatalogExists() {
        String catalogId = "catalog-id-1";
        Flux<DataService> dataServices = Flux.fromIterable(TestData.createDataServices(catalogId));

        when(dataServiceMongoRepository.findAllByOrganizationIdAndStatus(catalogId, Status.PUBLISHED)).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogModel(catalogId).block();
        Model expectedModel = RDFDataMgr.loadModel("catalog.ttl");

        assertNotNull(model);
        assertNotNull(expectedModel);

        boolean isomorphic = model.isIsomorphicWith(expectedModel);
        if(!isomorphic) {
            assertEquals(
                    dcatApNoModelService.serialise(Objects.requireNonNull(expectedModel), Lang.TURTLE),
                    dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.TURTLE));
        }
    }

    @Test
    void mustCorrectlySerialiseCatalogsModelAsRDF() {
        Flux<DataService> dataServices = Flux.merge(
                Flux.fromIterable(TestData.createDataServices("catalog-id-1")),
                Flux.fromIterable(TestData.createDataServices("catalog-id-2"))
        );

        when(dataServiceMongoRepository.findAllByStatus(Status.PUBLISHED)).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogsModel().block();
        String turtle = dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.TURTLE);
        String n3 = dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.N3);
        String nTriples = dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.NTRIPLES);
        String nQuads = dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.NQUADS);
        String trig = dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.TRIG);

        Model deserializedTurtle = ModelFactory.createDefaultModel().read(new StringReader(turtle), null, Lang.TURTLE.getName());
        Model deserializedN3 = ModelFactory.createDefaultModel().read(new StringReader(n3), null, Lang.N3.getName());
        Model deserializedNTriples = ModelFactory.createDefaultModel().read(new StringReader(nTriples), null, Lang.NTRIPLES.getName());
        Model deserializedNQuads = ModelFactory.createDefaultModel().read(new StringReader(nQuads), null, Lang.NQUADS.getName());
        Model deserializedTrig = ModelFactory.createDefaultModel().read(new StringReader(trig), null, Lang.TRIG.getName());

        Model expectedModel = RDFDataMgr.loadModel("catalogs.ttl");

        assertTrue(deserializedTurtle.isIsomorphicWith(expectedModel));
        assertTrue(deserializedN3.isIsomorphicWith(expectedModel));
        assertTrue(deserializedNTriples.isIsomorphicWith(expectedModel));
        assertTrue(deserializedNQuads.isIsomorphicWith(expectedModel));
        assertTrue(deserializedTrig.isIsomorphicWith(expectedModel));
    }

    @Test
    void mustCorrectlySerialiseCatalogModelAsRDF() {
        String catalogId = "catalog-id-1";
        Flux<DataService> dataServices = Flux.fromIterable(TestData.createDataServices(catalogId));

        when(dataServiceMongoRepository.findAllByOrganizationIdAndStatus(catalogId, Status.PUBLISHED)).thenReturn(dataServices);

        Model model = dcatApNoModelService.buildCatalogModel(catalogId).block();
        String turtle = dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.TURTLE);
        String rdfXML = dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.RDFXML);
        String rdfJSON = dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.RDFJSON);
        String ldJSON = dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.JSONLD);
        String trix = dcatApNoModelService.serialise(Objects.requireNonNull(model), Lang.TRIX);

        Model deserializedTurtle = ModelFactory.createDefaultModel().read(new StringReader(turtle), null, Lang.TURTLE.getName());
        Model deserializedRdfXml = ModelFactory.createDefaultModel().read(new StringReader(rdfXML), null, Lang.RDFXML.getName());
        Model deserializedRdfJson = ModelFactory.createDefaultModel().read(new StringReader(rdfJSON), null, Lang.RDFJSON.getName());
        Model deserializedLdJson = ModelFactory.createDefaultModel().read(new StringReader(ldJSON), null, Lang.JSONLD.getName());
        Model deserializedTrix = ModelFactory.createDefaultModel().read(new StringReader(trix), null, Lang.TRIX.getName());
        Model expectedModel = RDFDataMgr.loadModel("catalog.ttl");

        assertTrue(deserializedTurtle.isIsomorphicWith(expectedModel));
        assertTrue(deserializedRdfXml.isIsomorphicWith(expectedModel));
        assertTrue(deserializedRdfJson.isIsomorphicWith(expectedModel));
        assertTrue(deserializedLdJson.isIsomorphicWith(expectedModel));
        assertTrue(deserializedTrix.isIsomorphicWith(expectedModel));
    }
}
