package no.fdk.dataservicecatalog.service.parser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.converter.SwaggerConverter;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.exceptions.ParseException;

public class SwaggerParser implements Parser {
    public boolean canParse(String spec) {
        return Parser.isValidSwaggerOrOpenApiV3(spec, ApiType.SWAGGER, "2");
    }

    public OpenAPI parseToOpenAPI(String spec) throws ParseException {
        try {
            return new SwaggerConverter().readContents(spec, null, null).getOpenAPI();
        } catch (Throwable e) {
            throw new ParseException("Error parsing spec as Swagger v2 json: " + e.getMessage());
        }
    }

    public ApiSpecification parse(String spec) throws ParseException {
        OpenAPI openAPI = parseToOpenAPI(spec);
        ApiSpecification apiSpecification = OpenAPIToApiSpecificationConverter.convert(openAPI);

        // TODO parse formats

        return apiSpecification;
    }
}
