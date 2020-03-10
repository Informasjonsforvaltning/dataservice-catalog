package no.fdk.dataservicecatalog.dto.shared.apispecification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalDocumentation {

    private String description;
    private String url;

}
