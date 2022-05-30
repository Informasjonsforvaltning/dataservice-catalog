package no.fdk.dataservicecatalog.service.parser;

import io.swagger.v3.oas.models.OpenAPI;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.exceptions.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class OpenApiV3JsonParserTest {

    private static String spec;
    private static String invalidSpec;
    private final OpenApiV3Parser openApiV3JsonParser = new OpenApiV3Parser();

    @BeforeAll
    public static void setup() throws IOException {
        spec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3.json").getInputStream(), StandardCharsets.UTF_8);
        invalidSpec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3-invalid-missing-title.json").getInputStream(), StandardCharsets.UTF_8);
    }

    @Test
    public void CanParse_ShouldReturnTrue() throws ParseException {
        boolean result = openApiV3JsonParser.canParse(ParserUtils.readMandatoryMetaProperties(spec));
        assertTrue(result);
    }

    @Test
    public void CanParse_ShouldThrowException() {
        assertThrows(ParseException.class, () -> openApiV3JsonParser.canParse(ParserUtils.readMandatoryMetaProperties(invalidSpec)));
    }

    @Test
    public void ParseToOpenAPI_ShouldParse() throws Exception {
        OpenAPI parsed = openApiV3JsonParser.parseToOpenAPI(spec);
        assertEquals(parsed.getInfo().getTitle(), "Åpne Data fra Enhetsregisteret - API Dokumentasjon");
    }

    @Test
    public void Parse_ShouldParse() throws Exception {
        ApiSpecification parsed = openApiV3JsonParser.parse(spec);
        assertEquals(parsed.getInfo().getTitle(), "Åpne Data fra Enhetsregisteret - API Dokumentasjon");
    }

    @Test
    public void Parse_ShouldExtractFormats() throws Exception {
        ApiSpecification parsed = openApiV3JsonParser.parse(spec);
        assertEquals(parsed.getFormats().size(), 3);
        assertEquals(parsed.getFormats().toArray()[0], "application/json");
    }

}
