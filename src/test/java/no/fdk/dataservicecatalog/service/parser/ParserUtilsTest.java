package no.fdk.dataservicecatalog.service.parser;

import no.fdk.dataservicecatalog.exceptions.ParseException;
import no.fdk.dataservicecatalog.model.ApiType;
import no.fdk.dataservicecatalog.model.OpenAPIInfo;
import no.fdk.dataservicecatalog.model.OpenAPIMeta;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


@Tag("unit")
public class ParserUtilsTest {

    @Test
    public void isValid_shouldThrowException() {
        assertThrows(ParseException.class, () -> ParserUtils.isValidSwaggerOrOpenApiV3(null, ApiType.OPENAPI, "3"));
        assertThrows(ParseException.class, () -> ParserUtils.isValidSwaggerOrOpenApiV3(null, ApiType.SWAGGER, "2"));

        OpenAPIInfo specInfo = OpenAPIInfo.builder().title("title").version("version").build();
        OpenAPIMeta specMissingVersion = OpenAPIMeta.builder().info(specInfo).build();
        assertThrows(ParseException.class, () -> ParserUtils.isValidSwaggerOrOpenApiV3(specMissingVersion, ApiType.OPENAPI, "3"));
        assertThrows(ParseException.class, () -> ParserUtils.isValidSwaggerOrOpenApiV3(specMissingVersion, ApiType.SWAGGER, "2"));

        OpenAPIInfo specInfoMissingTitle = OpenAPIInfo.builder().version("version").build();
        OpenAPIMeta specMissingTitle = OpenAPIMeta.builder().openapi("3.0.0").swagger("2.0.0").info(specInfoMissingTitle).build();
        assertThrows(ParseException.class, () -> ParserUtils.isValidSwaggerOrOpenApiV3(specMissingTitle, ApiType.OPENAPI, "3"));
        assertThrows(ParseException.class, () -> ParserUtils.isValidSwaggerOrOpenApiV3(specMissingTitle, ApiType.SWAGGER, "2"));

        OpenAPIInfo specInfoMissingDocumentVersion = OpenAPIInfo.builder().title("title").build();
        OpenAPIMeta specMissingDocumentVersion = OpenAPIMeta.builder().openapi("3.0.0").swagger("2.0.0").info(specInfoMissingDocumentVersion).build();
        assertThrows(ParseException.class, () -> ParserUtils.isValidSwaggerOrOpenApiV3(specMissingDocumentVersion, ApiType.OPENAPI, "3"));
        assertThrows(ParseException.class, () -> ParserUtils.isValidSwaggerOrOpenApiV3(specMissingDocumentVersion, ApiType.SWAGGER, "2"));
    }

}
