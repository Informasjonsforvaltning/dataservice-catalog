package no.fdk.dataservicecatalog.dto.shared.apispecification.paths;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ExternalDocumentation;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
class Operation {

    private String summary;
    private String description;
    private ExternalDocumentation externalDocs;

}
