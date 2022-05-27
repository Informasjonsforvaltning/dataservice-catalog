package no.fdk.dataservicecatalog.service.parser;

import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class OpenAPIToApiSpecificationConverterTest {

    private static String spec;
    private static String yamlSpec;
    private final SwaggerParser swaggerJsonParser = new SwaggerParser();

    @BeforeAll
    public static void setup() throws IOException {
        spec = IOUtils.toString(new ClassPathResource("openapi-to-spec-datetime.json").getInputStream(),
                StandardCharsets.UTF_8);
        yamlSpec = IOUtils.toString(new ClassPathResource("openapi-to-spec-datetime.yaml").getInputStream(),
                StandardCharsets.UTF_8);
    }

    @Test
    public void CanParse_ShouldReturnTrue() {
        boolean result = swaggerJsonParser.canParse(spec);
        assertTrue(result);
    }

    @Test
    public void CanParseYAML_ShouldReturnTrue() {
        boolean result = swaggerJsonParser.canParse(yamlSpec);
        assertTrue(result);
    }

    @Test
    public void ParseToOpenAPI_ShouldParse() throws Exception {
        OpenAPI parsed = swaggerJsonParser.parseToOpenAPI(spec);
        assertEquals(parsed.getInfo().getTitle(), "datetimetest");
    }

    @Test
    public void ParseYAMLToOpenAPI_ShouldParse() throws Exception {
        OpenAPI parsed = swaggerJsonParser.parseToOpenAPI(yamlSpec);
        assertEquals(parsed.getInfo().getTitle(), "datetimetest");
    }

    @Test
    public void Convert_ShouldConvert() throws Exception {
        OpenAPI parsed = swaggerJsonParser.parseToOpenAPI(spec);
        OpenAPIToApiSpecificationConverter.convert(parsed);
    }

    @Test
    public void ConvertYAML_ShouldConvert() throws Exception {
        OpenAPI parsed = swaggerJsonParser.parseToOpenAPI(yamlSpec);
        OpenAPIToApiSpecificationConverter.convert(parsed);
    }
}
