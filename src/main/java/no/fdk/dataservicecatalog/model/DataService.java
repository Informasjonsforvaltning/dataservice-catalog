package no.fdk.dataservicecatalog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fdk.dataservicecatalog.dto.acat.Access;
import no.fdk.dataservicecatalog.dto.acat.Status;
import no.fdk.dataservicecatalog.dto.acat.TermsAndConditions;
import no.fdk.dataservicecatalog.dto.shared.apispecification.info.Contact;
import no.fdk.dataservicecatalog.dto.shared.apispecification.info.License;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.MediaType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "dataservices")
public class DataService {

    public static final String DEFAULT_LANGUAGE = "no";

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime created;
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime modified;
    @Id
    private String id;
    private String catalogId;
    @NotEmpty
    private Map<String, String> title;
    private String version;
    private Integer operationCount;
    private Contact contact;
    @NotBlank
    private String endpointUrl;
    private MediaType format;
    private Map<String, String> description;
    private String endpointDescription;
    private License license;
    private Access access;
    private TermsAndConditions termsAndConditions;
    private Status status;
    private String serviceType;
    private Set<String> servesDataset;

}
