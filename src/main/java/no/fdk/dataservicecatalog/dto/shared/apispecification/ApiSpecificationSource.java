package no.fdk.dataservicecatalog.dto.shared.apispecification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiSpecificationSource {

    private String apiSpecUrl;

}
