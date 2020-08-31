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
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.util.URIref;
import org.apache.jena.vocabulary.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.StringWriter;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Map.entry;
import static org.apache.jena.util.FileUtils.isURI;

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
        return ModelFactory
                .createDefaultModel()
                .setNsPrefixes(
                        Map.ofEntries(
                                entry("dcat", DCAT.NS),
                                entry("dct", DCTerms.NS),
                                entry("rdf", RDF.uri),
                                entry("vcard", VCARD4.NS)
                        )
                );
    }

    private String getCatalogUri(String catalogId) {
        return format("%s/catalogs/%s", applicationProperties.getCatalogBaseUri(), catalogId);
    }

    private String getDataServiceUri(String dataServiceId) {
        return format("%s/data-services/%s", applicationProperties.getCatalogBaseUri(), dataServiceId);
    }

    private String getPublisherUri(String publisherId) {
        return format("https://data.brreg.no/enhetsregisteret/api/enheter/%s", publisherId);
    }

    private String getOrganizationsCatalogUri(String publisherId) {
        return format("%s/organizations/%s", applicationProperties.getOrgCatalogUri(), publisherId);
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
                .addProperty(DCTerms.publisher, ResourceFactory.createResource(URIref.encode(getOrganizationsCatalogUri(catalog.getId()))))
                .addProperty(DCTerms.title, ResourceFactory.createLangLiteral(format("Data service catalog (%s)", catalog.getId()), "en"));

        model.createResource(URIref.encode(getOrganizationsCatalogUri(catalog.getId())))
            .addProperty(RDF.type, FOAF.Agent)
            .addProperty(DCTerms.identifier, catalog.getId())
            .addProperty(OWL.sameAs, URIref.encode(getPublisherUri(catalog.getId())));
    }

    private void addDataServiceToModel(Model model, DataService dataService) {
        Resource dataServiceResource = model.createResource(URIref.encode(getDataServiceUri(dataService.getId())))
                .addProperty(RDF.type, DCAT.DataService);

        if (dataService.getTitle() != null) {
            dataService.getTitle().forEach((key, value) -> {
                if (key != null && value != null && !value.isBlank()) {
                    dataServiceResource.addProperty(DCTerms.title, ResourceFactory.createLangLiteral(value, key));
                }
            });
        }

        if (dataService.getDescription() != null) {
            dataService.getDescription().forEach((key, value) -> {
                if (key != null && value != null && !value.isBlank()) {
                    dataServiceResource.addProperty(DCTerms.description, ResourceFactory.createLangLiteral(value, key));
                }
            });
        }

        if (dataService.getEndpointDescriptions() != null) {
            dataService.getEndpointDescriptions().forEach(value -> {
                if (value != null && isURI(value)) {
                    dataServiceResource.addProperty(DCAT.endpointDescription, ResourceFactory.createResource(URIref.encode(value)));
                }
            });
        }

        if (dataService.getEndpointUrls() != null) {
            dataService.getEndpointUrls().forEach(value -> {
                if (value != null && isURI(value)) {
                    dataServiceResource.addProperty(DCAT.endpointURL, ResourceFactory.createResource(URIref.encode(value)));
                }
            });
        }

        if (dataService.getContact() != null) {
            Contact contact = dataService.getContact();
            Resource contactPointResource = model.createResource()
                    .addProperty(RDF.type, VCARD4.Organization)
                    .addProperty(VCARD4.fn, format("Contact information | (%s)", dataService.getOrganizationId()));

            if (contact.getName() != null && !contact.getName().isBlank()) {
                contactPointResource.addProperty(VCARD4.hasOrganizationName, ResourceFactory.createLangLiteral(contact.getName(), "nb"));
            }

            if (contact.getEmail() != null && !contact.getEmail().isBlank()) {
                contactPointResource.addProperty(VCARD4.hasEmail, ResourceFactory.createResource(URIref.encode(format("mailto:%s", contact.getEmail()))));
            }

            if (contact.getUrl() != null && isURI(contact.getUrl())) {
                contactPointResource.addProperty(VCARD4.hasURL, ResourceFactory.createResource(URIref.encode(contact.getUrl())));
            }

            if (contact.getPhone() != null && !contact.getPhone().isBlank()) {
                Resource telephoneResource = model.createResource()
                        .addProperty(RDF.type, VCARD4.TelephoneType)
                        .addProperty(VCARD4.hasValue, ResourceFactory.createResource(URIref.encode(format("tel:%s", contact.getPhone()))));

                contactPointResource.addProperty(VCARD4.hasTelephone, telephoneResource);
            }

            dataServiceResource.addProperty(DCAT.contactPoint, contactPointResource);
        }

        if (dataService.getMediaTypes() != null) {
            dataService.getMediaTypes().forEach(mediaType -> {
                if (mediaType != null && !mediaType.isBlank()) {
                    dataServiceResource.addProperty(
                        DCAT.mediaType,
                        ResourceFactory.createResource(URIref.encode(format("https://www.iana.org/assignments/media-types/%s", mediaType)))
                    );
                }
            });
        }

        if (dataService.getServesDataset() != null) {
            dataService.getServesDataset().forEach(dataset -> {
                if (dataset != null && isURI(dataset)) {
                    dataServiceResource
                        .addProperty(
                            DCAT.servesDataset,
                            ResourceFactory.createResource(URIref.encode(dataset))
                        );
                }
            });
        }

        if (dataService.getServiceType() != null && !dataService.getServiceType().isBlank()) {
            dataServiceResource.addProperty(
                    DCTerms.conformsTo,
                    ResourceFactory.createResource(URIref.encode(format("https://data.norge.no/def/serviceType#%s", dataService.getServiceType())))
            );
        }

        model.getProperty(URIref.encode(getCatalogUri(dataService.getOrganizationId())))
                .addProperty(DCAT.service, dataServiceResource);
    }
}
