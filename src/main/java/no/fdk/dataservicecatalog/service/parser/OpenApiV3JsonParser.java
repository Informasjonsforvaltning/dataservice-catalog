package no.fdk.dataservicecatalog.service.parser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.OpenAPIV3Parser;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.exceptions.ParseException;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenApiV3JsonParser implements Parser {

    public boolean canParse(String spec) {
        return Parser.isValidSwaggerOrOpenApiV3(spec, ApiType.OPENAPI, "3");
    }

    public OpenAPI parseToOpenAPI(String spec) throws ParseException {
        try {
            return new OpenAPIV3Parser().readContents(spec, null, null).getOpenAPI();
        } catch (Throwable e) {
            throw new ParseException("Error parsing spec as OpenApi v3 json: " + e.getMessage());
        }
    }

    Set<String> extractFormatsFromOpenAPI(OpenAPI openAPI) {
        return Stream.ofNullable(openAPI.getPaths())
                .map(Paths::values).flatMap(Collection::stream)
                .map(PathItem::readOperations)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(Operation::getResponses)
                .map(ApiResponses::values).flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(ApiResponse::getContent)
                .filter(Objects::nonNull)
                .map(Content::keySet).flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public ApiSpecification parse(String spec) throws ParseException {
        OpenAPI openAPI = parseToOpenAPI(spec);
        ApiSpecification apiSpecification = OpenAPIToApiSpecificationConverter.convert(openAPI);

        apiSpecification.setFormats(extractFormatsFromOpenAPI(openAPI));

        return apiSpecification;
    }
}
