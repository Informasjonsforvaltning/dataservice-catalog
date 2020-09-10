package no.fdk.dataservicecatalog.service.parser;

import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class UniversalParserTest {

    private final Parser parser = new UniversalParser();

    @Test
    public void CanParse_WhenSwagger_ShouldReturnTrue() throws IOException {
        String spec = IOUtils.toString(new ClassPathResource("fs-api-swagger.json").getInputStream(), "UTF-8");
        boolean result = parser.canParse(spec);
        assertTrue(result);
    }

    @Test
    public void CanParse_WhenOpenApi_ShouldReturnTrue() throws IOException {
        String spec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3.json").getInputStream(), "UTF-8");
        boolean result = parser.canParse(spec);
        assertTrue(result);
    }

    @Test
    public void CanParse_WhenSwagger_ShouldReturnFalse() throws IOException {
        String spec = IOUtils.toString(new ClassPathResource("fs-api-swagger-invalid-missing-title.json").getInputStream(), "UTF-8");
        boolean result = parser.canParse(spec);
        assertFalse(result);
    }

    @Test
    public void CanParse_WhenOpenApi_ShouldReturnFalseWhen() throws IOException {
        String spec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3-invalid-missing-title.json").getInputStream(), "UTF-8");
        boolean result = parser.canParse(spec);
        assertFalse(result);
    }

    @Test
    public void Parse_WhenSwagger_ShouldParse() throws Exception {
        String spec = IOUtils.toString(new ClassPathResource("fs-api-swagger.json").getInputStream(), "UTF-8");

        ApiSpecification parsed = parser.parse(spec);
        assertEquals("FS-API", parsed.getInfo().getTitle());
    }

    @Test
    public void Parse_WhenOpenApi_ShouldParse() throws Exception {
        String spec = IOUtils.toString(new ClassPathResource("enhetsregisteret-openapi3.json").getInputStream(), "UTF-8");

        ApiSpecification parsed = parser.parse(spec);
        assertEquals("Åpne Data fra Enhetsregisteret - API Dokumentasjon", parsed.getInfo().getTitle());
    }

}
