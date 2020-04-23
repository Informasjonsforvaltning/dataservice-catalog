package no.fdk.dataservicecatalog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fdk.dataservicecatalog.config.ApplicationProperties;
import no.fdk.dataservicecatalog.dto.shared.apispecification.info.Contact;
import no.fdk.dataservicecatalog.model.Catalog;
import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.model.Status;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.util.URIref;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD4;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.StringWriter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DcatApNoModelService {
    private final ApplicationProperties applicationProperties;
    private final DataServiceMongoRepository dataServiceMongoRepository;

    public Mono<Model> buildCatalogsModel() {
        Flux<DataService> dataServicesFlux = dataServiceMongoRepository
                .findAllByStatus(Status.PUBLISHED)
                .doOnError(error -> log.error("Failed to load data services", error));
        dataServicesFlux.count().subscribe(count -> log.info("Successfully loaded {} data services", count));
        return buildCatalogsModel(dataServicesFlux);
    }

    public Mono<Model> buildCatalogModel(String catalogId) {
        Flux<DataService> dataServicesFlux = dataServiceMongoRepository
                .findAllByOrganizationIdAndStatus(catalogId, Status.PUBLISHED)
                .doOnError(error -> log.error("Failed to load data services for catalog with ID {}", catalogId, error));
        dataServicesFlux.count().subscribe(count -> log.info("Successfully loaded {} data services for catalog with ID {}", count, catalogId));
        return buildCatalogsModel(dataServicesFlux);
    }

    public String serialise(Model model) {
        StringWriter stringWriter = new StringWriter();
        model.write(stringWriter, "TURTLE");
        return stringWriter.getBuffer().toString();
    }

    private Model createModel() {
        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("vcard", "http://www.w3.org/2006/vcard/ns#");

        return model;
    }

    private String getCatalogUri(String catalogId) {
        return String.format("%s/catalogs/%s", applicationProperties.getCatalogBaseUri(), catalogId);
    }

    private String getDataServiceUri(String dataServiceId) {
        return String.format("%s/data-services/%s", applicationProperties.getCatalogBaseUri(), dataServiceId);
    }

    private String getPublisherUri(String publisherId) {
        return String.format("https://data.brreg.no/enhetsregisteret/api/enheter/%s", publisherId);
    }

    private Mono<Model> buildCatalogsModel(Flux<DataService> dataServicesFlux) {
        Model model = createModel();
        return dataServicesFlux
                .groupBy(DataService::getOrganizationId)
                .doOnNext(entry -> addCatalogToModel(model, Catalog.builder().id(entry.key()).build()))
                .flatMap(Flux::collectList)
                .doOnNext(dataServices -> dataServices.forEach(dataService -> addDataServiceToModel(model, dataService)))
                .then()
                .thenReturn(model);
    }

    private void addCatalogToModel(Model model, Catalog catalog) {
        model.createResource(URIref.encode(getCatalogUri(catalog.getId())))
                .addProperty(RDF.type, DCAT.Catalog)
                .addProperty(DCTerms.publisher, ResourceFactory.createResource(URIref.encode(getPublisherUri(catalog.getId()))))
                .addProperty(DCTerms.title, ResourceFactory.createLangLiteral(String.format("Data service catalog (%s)", catalog.getId()), "en"));
    }

    private void addDataServiceToModel(Model model, DataService dataService) {
        Resource dataServiceResource = model.createResource(URIref.encode(getDataServiceUri(dataService.getId())))
                .addProperty(RDF.type, DCAT.DataService);

        if (dataService.getTitle() != null) {
            dataService.getTitle().forEach((key, value) -> dataServiceResource.addProperty(DCTerms.title, ResourceFactory.createLangLiteral(value, key)));
        }

        if (dataService.getDescription() != null) {
            dataService.getDescription().forEach((key, value) -> dataServiceResource.addProperty(DCTerms.description, ResourceFactory.createLangLiteral(value, key)));
        }

        if (dataService.getEndpointDescriptions() != null) {
            dataService.getEndpointDescriptions().forEach(value -> dataServiceResource.addProperty(DCAT.endpointDescription, ResourceFactory.createResource(URIref.encode(value))));
        }

        if (dataService.getEndpointUrl() != null) {
            dataServiceResource.addProperty(DCAT.endpointURL, ResourceFactory.createResource(URIref.encode(dataService.getEndpointUrl())));
        }

        if (dataService.getContact() != null) {
            Contact contact = dataService.getContact();
            Resource contactPointResource = model.createResource()
                    .addProperty(RDF.type, VCARD4.Organization);

            if (contact.getName() != null) {
                contactPointResource.addProperty(VCARD4.hasOrganizationName, ResourceFactory.createLangLiteral(contact.getName(), "nb"));
            }

            if (contact.getEmail() != null) {
                contactPointResource.addProperty(VCARD4.hasEmail, ResourceFactory.createResource(URIref.encode(String.format("mailto:%s", contact.getEmail()))));
            }

            if (contact.getUrl() != null) {
                contactPointResource.addProperty(VCARD4.hasURL, ResourceFactory.createResource(URIref.encode(contact.getUrl())));
            }

            dataServiceResource.addProperty(DCAT.contactPoint, contactPointResource);
        }

        model.getProperty(URIref.encode(getCatalogUri(dataService.getOrganizationId())))
                .addProperty(DCAT.service, dataServiceResource);
    }
}
