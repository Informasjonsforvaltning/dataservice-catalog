package no.fdk.dataservicecatalog.dto.acat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TermsAndConditions {

    private String cost;

    private String usageLimitation;

    private String performance;

    private String availability;

}
