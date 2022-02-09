package no.fdk.dataservicecatalog.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.fdk.dataservicecatalog.model.Catalog;
import no.fdk.dataservicecatalog.utils.ApiTestContext;
import no.fdk.dataservicecatalog.utils.TestUtils;
import no.fdk.dataservicecatalog.utils.jwk.Access;
import no.fdk.dataservicecatalog.utils.jwk.JwtToken;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
        properties = "spring.profiles.active=integration-test",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(initializers = ApiTestContext.Initializer.class)
@Tag("integration")
public class CatalogIntegrationTest extends ApiTestContext {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getCompleteListForSysAdmin() throws JsonProcessingException {
        List<Catalog> expected = List.of(
                Catalog.builder().id("123456789").dataServiceCount(15).build(),
                Catalog.builder().id("987654321").dataServiceCount(15).build());

        String rsp = TestUtils.authorizedGet("/catalogs", port, JwtToken.buildToken(Access.ROOT));
        List<Catalog> result = objectMapper.readValue(rsp, new TypeReference<>(){});

        assertEquals(expected, result);
    }

    @Test
    void getPermittedListForWriteAccess() throws JsonProcessingException {
        List<Catalog> expected = List.of(Catalog.builder().id("123456789").dataServiceCount(15).build());

        String rsp = TestUtils.authorizedGet("/catalogs", port, JwtToken.buildToken(Access.ORG_WRITE));
        List<Catalog> result = objectMapper.readValue(rsp, new TypeReference<>(){});

        assertEquals(expected, result);
    }

    @Test
    void getPermittedListForReadAccess() throws JsonProcessingException {
        List<Catalog> expected = List.of(Catalog.builder().id("123456789").dataServiceCount(15).build());

        String rsp = TestUtils.authorizedGet("/catalogs", port, JwtToken.buildToken(Access.ORG_READ));
        List<Catalog> result = objectMapper.readValue(rsp, new TypeReference<>(){});

        assertEquals(expected, result);
    }

}
