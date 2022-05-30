package no.fdk.dataservicecatalog.service.parser;

import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.exceptions.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class UniversalParserTest {

    private final UniversalParser parser = new UniversalParser();

    @Test
    public void CanParse_WhenSwagger_ShouldReturnTrue() throws Exception {
        String spec = IOUtils.toString(new ClassPathResource("fs-api-swagger.json").getInputStream(), "UTF-8");
        String yamlSpec = IOUtils.toString(new ClassPathResource("fs-api-swagger.yaml").getInputStream(), "UTF-8");
        assertTrue(parser.canParse(spec));
        assertTrue(parser.canParse(yamlSpec));
    }

    @Test
    public void CanParse_WhenOpenApi_ShouldReturnTrue() throws Exception {
        String spec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3.json").getInputStream(), "UTF-8");
        String yamlSpec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3.yaml").getInputStream(), "UTF-8");
        assertTrue(parser.canParse(spec));
        assertTrue(parser.canParse(yamlSpec));
    }

    @Test
    public void CanParse_WhenSwagger_ShouldThrowParseException() throws IOException {
        String spec = IOUtils.toString(new ClassPathResource("fs-api-swagger-invalid-missing-title.json").getInputStream(), "UTF-8");
        String yamloSpec = IOUtils.toString(new ClassPathResource("fs-api-swagger-invalid-missing-title.yaml").getInputStream(), "UTF-8");
        assertThrows(ParseException.class, () -> parser.canParse(spec));
        assertThrows(ParseException.class, () -> parser.canParse(yamloSpec));
    }

    @Test
    public void CanParse_WhenOpenApi_ShouldThrowParseException() throws IOException {
        String spec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3-invalid-missing-title.json").getInputStream(), "UTF-8");
        String yamlSpec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3-invalid-missing-title.yaml").getInputStream(), "UTF-8");
        assertThrows(ParseException.class, () -> parser.canParse(spec));
        assertThrows(ParseException.class, () -> parser.canParse(yamlSpec));
    }

    @Test
    public void CanParse_InvalidInfo_ShouldThrowParseException() throws IOException {
        String spec = IOUtils.toString(new ClassPathResource("openapi3-invalid-info.json").getInputStream(), "UTF-8");
        String yamloSpec = IOUtils.toString(new ClassPathResource("openapi3-invalid-info.yaml").getInputStream(), "UTF-8");
        assertThrows(ParseException.class, () -> parser.canParse(spec));
        assertThrows(ParseException.class, () -> parser.canParse(yamloSpec));
    }

    @Test
    public void Parse_WhenSwagger_ShouldParse() throws Exception {
        String spec = IOUtils.toString(new ClassPathResource("fs-api-swagger.json").getInputStream(), "UTF-8");
        ApiSpecification parsed = parser.parse(spec);
        assertEquals("FS-API", parsed.getInfo().getTitle());

        String yamlSpec = IOUtils.toString(new ClassPathResource("fs-api-swagger.yaml").getInputStream(), "UTF-8");
        ApiSpecification parsedYAML = parser.parse(yamlSpec);
        assertEquals("FS-API", parsedYAML.getInfo().getTitle());
    }

    @Test
    public void Parse_WhenOpenApi_ShouldParse() throws Exception {
        String spec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3.json").getInputStream(), "UTF-8");
        ApiSpecification parsed = parser.parse(spec);
        assertEquals("Åpne Data fra Enhetsregisteret - API Dokumentasjon", parsed.getInfo().getTitle());

        String yamlSpec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3.yaml").getInputStream(), "UTF-8");
        ApiSpecification parsedYAML = parser.parse(yamlSpec);
        assertEquals("Åpne Data fra Enhetsregisteret - API Dokumentasjon", parsedYAML.getInfo().getTitle());
    }

}
