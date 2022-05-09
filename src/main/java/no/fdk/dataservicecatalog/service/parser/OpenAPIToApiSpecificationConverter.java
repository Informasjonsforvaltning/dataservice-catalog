package no.fdk.dataservicecatalog.service.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.v3.oas.models.OpenAPI;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.exceptions.ParseException;

public class OpenAPIToApiSpecificationConverter {
    public static ApiSpecification convert(OpenAPI openAPI) throws ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            String json = objectMapper.writeValueAsString(openAPI);
            return objectMapper.readValue(json, ApiSpecification.class);
        } catch (Throwable e) {
            throw new ParseException("Error converting OpenAPI to ApiSpecification: " + e.getMessage());
        }
    }
}
