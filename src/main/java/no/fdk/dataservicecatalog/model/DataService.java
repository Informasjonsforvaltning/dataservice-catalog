package no.fdk.dataservicecatalog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fdk.dataservicecatalog.dto.acat.Access;
import no.fdk.dataservicecatalog.dto.acat.DataServiceStatus;
import no.fdk.dataservicecatalog.dto.acat.TermsAndConditions;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ExternalDocumentation;
import no.fdk.dataservicecatalog.dto.shared.apispecification.info.Contact;
import no.fdk.dataservicecatalog.dto.shared.apispecification.info.License;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "dataservices")
public class DataService {

    public static final String DEFAULT_LANGUAGE = "nb";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime modified;
    @Id
    private String id;
    private String organizationId;
    @NotEmpty
    private Map<String, String> title;
    private String version;
    private Integer operationCount;
    private Contact contact;
    private License license;
    private List<String> endpointUrls;
    private Set<String> mediaTypes;
    private Map<String, String> description;
    private List<String> endpointDescriptions;
    private Access access;
    private TermsAndConditions termsAndConditions;
    private DataServiceStatus dataServiceStatus;
    private String serviceType;
    private Set<String> servesDataset;
    private Status status;
    private ExternalDocumentation externalDocs;
    private String termsOfServiceUrl;

    //dct:accessRights
    //Norwegian: tilgangsnivå
    private String accessRights;

    //dcat:keyword
    //Norwegian: emneord
    private List<Map<String, String>> keywords;

    //dcat:landingPage
    //Norwegian: landingsside
    private String landingPage;

    //foaf:page
    //Norwegian: dokumentasjon
    private List<String> pages;

    //dcat:theme
    //Norwegian: tema
    private List<String> themes;

    //dct:type
    //Norwegian: type
    private String type;

    private boolean imported = false;

}
