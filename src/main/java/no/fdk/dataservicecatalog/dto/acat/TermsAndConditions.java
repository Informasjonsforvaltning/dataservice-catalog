package no.fdk.dataservicecatalog.dto.acat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TermsAndConditions {

    private String price;

    private Map<String, String> usageLimitation;

    private String capacityAndPerformance;

    private String reliability;

}
