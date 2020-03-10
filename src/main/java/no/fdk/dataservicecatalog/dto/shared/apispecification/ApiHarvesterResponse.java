package no.fdk.dataservicecatalog.dto.shared.apispecification;

import lombok.Data;

import java.util.List;

@Data
public class ApiHarvesterResponse {

    ApiSpecification apiSpecification;

    List<String> messages;
}
