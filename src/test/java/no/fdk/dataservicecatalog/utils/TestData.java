package no.fdk.dataservicecatalog.utils;

import no.fdk.dataservicecatalog.dto.shared.apispecification.info.Contact;
import no.fdk.dataservicecatalog.model.DataService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Map.entry;

public class TestData {
    public static List<DataService> createDataServices(String organizationId) {
        return List.of(
                DataService
                        .builder()
                        .id(format("%s/id-1", organizationId))
                        .organizationId(organizationId)
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-2", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-2-title-en")
                                )
                        )
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-3", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-3-title-en"),
                                        entry("nb", "id-3-title-nb")
                                )
                        )
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-4", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-4-title-en"),
                                        entry("nb", "id-4-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-4-description-en")
                                )
                        )
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-5", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-5-title-en"),
                                        entry("nb", "id-5-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-5-description-en"),
                                        entry("nb", "id-5-description-nb")
                                )
                        )
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-6", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-6-title-en"),
                                        entry("nb", "id-6-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-6-description-en"),
                                        entry("nb", "id-6-description-nb")
                                )
                        )
                        .endpointDescriptions(List.of("http://endpoint-description-6"))
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-7", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-7-title-en"),
                                        entry("nb", "id-7-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-7-description-en"),
                                        entry("nb", "id-7-description-nb")
                                )
                        )
                        .endpointDescriptions(List.of("http://endpoint-description-7"))
                        .endpointUrls(List.of("http://endpoint-url-7-1", "http://endpoint-url-7-2"))
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-8", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-8-title-en"),
                                        entry("nb", "id-8-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-8-description-en"),
                                        entry("nb", "id-8-description-nb")
                                )
                        )
                        .endpointDescriptions(List.of("http://endpoint-description-8"))
                        .endpointUrls(List.of("http://endpoint-url-8-1", "http://endpoint-url-8-2"))
                        .contact(
                                Contact
                                        .builder()
                                        .name("organisation-name-8")
                                        .build()
                        )
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-9", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-9-title-en"),
                                        entry("nb", "id-9-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-9-description-en"),
                                        entry("nb", "id-9-description-nb")
                                )
                        )
                        .endpointDescriptions(List.of("http://endpoint-description-9-1", "http://endpoint-description-9-2", "http://endpoint-description-9-3"))
                        .endpointUrls(List.of("http://endpoint-url-9-1", "http://endpoint-url-9-2"))
                        .contact(
                                Contact
                                        .builder()
                                        .name("organisation-name-9")
                                        .email("email-9@email.email")
                                        .build()
                        )
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-10", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-10-title-en"),
                                        entry("nb", "id-10-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-10-description-en"),
                                        entry("nb", "id-10-description-nb")
                                )
                        )
                        .endpointDescriptions(List.of("http://endpoint-description-10"))
                        .endpointUrls(List.of("http://endpoint-url-10-1", "http://endpoint-url-10-2"))
                        .contact(
                                Contact
                                        .builder()
                                        .name("organisation-name-10")
                                        .email("email-10@email.email")
                                        .url("http://organisation-url-10")
                                        .build()
                        )
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-11", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-11-title-en"),
                                        entry("nb", "id-11-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-11-description-en"),
                                        entry("nb", "id-11-description-nb")
                                )
                        )
                        .endpointDescriptions(List.of("http://endpoint-description-11"))
                        .endpointUrls(List.of("http://endpoint-url-11-1", "http://endpoint-url-11-2"))
                        .contact(
                                Contact
                                        .builder()
                                        .name("organisation-name-11")
                                        .email("email-11@email.email")
                                        .url("http://organisation-url-11")
                                        .phone("phone-11")
                                        .build()
                        )
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-12", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-12-title-en"),
                                        entry("nb", "id-12-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-12-description-en"),
                                        entry("nb", "id-12-description-nb")
                                )
                        )
                        .endpointDescriptions(List.of("http://endpoint-description-12"))
                        .endpointUrls(List.of("http://endpoint-url-12-1", "http://endpoint-url-12-2"))
                        .contact(
                                Contact
                                        .builder()
                                        .name("organisation-name-12")
                                        .email("email-12@email.email")
                                        .url("http://organisation-url-12")
                                        .phone("phone-12")
                                        .build()
                        )
                        .mediaTypes(List.of("application/json", "text/xml"))
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-13", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-13-title-en"),
                                        entry("nb", "id-13-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-13-description-en"),
                                        entry("nb", "id-13-description-nb")
                                )
                        )
                        .endpointDescriptions(List.of("http://endpoint-description-13"))
                        .endpointUrls(List.of("http://endpoint-url-13-1", "http://endpoint-url-13-2"))
                        .contact(
                                Contact
                                        .builder()
                                        .name("organisation-name-13")
                                        .email("email-13@email.email")
                                        .url("http://organisation-url-13")
                                        .phone("phone-13")
                                        .build()
                        )
                        .mediaTypes(List.of("application/json", "text/xml"))
                        .servesDataset(Set.of("http://some-dataset-1", "http://some-dataset-2"))
                        .build(),
                DataService
                        .builder()
                        .id(format("%s/id-14", organizationId))
                        .organizationId(organizationId)
                        .title(
                                Map.ofEntries(
                                        entry("en", "id-14-title-en"),
                                        entry("nb", "id-14-title-nb")
                                )
                        )
                        .description(
                                Map.ofEntries(
                                        entry("en", "id-14-description-en"),
                                        entry("nb", "id-14-description-nb")
                                )
                        )
                        .endpointDescriptions(List.of("http://endpoint-description-14"))
                        .endpointUrls(List.of("http://endpoint-url-14-1", "http://endpoint-url-14-2"))
                        .contact(
                                Contact
                                        .builder()
                                        .name("organisation-name-14")
                                        .email("email-14@email.email")
                                        .url("http://organisation-url-14")
                                        .phone("phone-14")
                                        .build()
                        )
                        .mediaTypes(List.of("application/json", "text/xml"))
                        .servesDataset(Set.of("http://some-dataset-1", "http://some-dataset-2"))
                        .serviceType("CUSTOMER_RELATIONS")
                        .build()
        );
    }
}
