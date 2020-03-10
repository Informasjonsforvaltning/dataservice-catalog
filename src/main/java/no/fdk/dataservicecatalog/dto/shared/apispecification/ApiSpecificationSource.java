package no.fdk.dataservicecatalog.dto.shared.apispecification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiSpecificationSource {

    @URL
    private String apiSpecUrl;

}
