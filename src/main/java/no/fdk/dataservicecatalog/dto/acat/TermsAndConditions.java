package no.fdk.dataservicecatalog.dto.acat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TermsAndConditions {

    private Map<String, String> price;

    private Map<String, String> usageLimitation;

    private Map<String, String> capacityAndPerformance;

    private Map<String, String> reliability;

}
