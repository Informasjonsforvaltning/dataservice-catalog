package no.dcat.controller;

import no.dcat.model.ApiCatalog;
import no.dcat.service.ApiCatalogHarvesterService;
import no.dcat.service.ApiCatalogRepository;
import no.fdk.webutils.exceptions.NotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class ApiCatalogControllerTest {

    @Mock
    private ApiCatalogRepository apiCatalogRepositoryMock;

    @Mock
    private ApiCatalogHarvesterService apiCatalogHarvesterServiceMock;

    @InjectMocks
    private ApiCatalogController apiCatalogController;

    @Test
    public void getApiCatalog_ShouldReturnApiCatalog() throws NotFoundException {
        ApiCatalog existingApiCatalog = new ApiCatalog();
        String orgNr = "testOrgNr";
        when(apiCatalogRepositoryMock.findByOrgNo(orgNr)).thenReturn(Optional.of(existingApiCatalog));

        ApiCatalog apiCatalog = apiCatalogController.getApiCatalog(orgNr);

        assertEquals(apiCatalog, existingApiCatalog);
    }

    @Test
    public void getApiCatalog_WhenNotFound_ShouldThrowNotFoundException() throws NotFoundException {
        String orgNr = "testOrgNr";
        when(apiCatalogRepositoryMock.findByOrgNo(orgNr)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> apiCatalogController.getApiCatalog(orgNr));
    }

    @Test
    public void createApiCatalog_WhenApiCatalogFound_ShouldModifyExisting() {
        ApiCatalog existingApiCatalog = new ApiCatalog();
        String orgNr = "testOrgNr";
        String testHarvestSourceUri = "testUri";
        ApiCatalog postPayload = ApiCatalog.builder().harvestSourceUri(testHarvestSourceUri).build();

        when(apiCatalogRepositoryMock.findByOrgNo(orgNr)).thenReturn(Optional.of(existingApiCatalog));
        when(apiCatalogRepositoryMock.save(any(ApiCatalog.class))).thenAnswer((invocation) -> invocation.getArguments()[0]);

        ApiCatalog apiCatalogSaved = apiCatalogController.createApiCatalog(orgNr, postPayload);

        assertEquals(apiCatalogSaved, existingApiCatalog);
        assertEquals(apiCatalogSaved.getHarvestSourceUri(), testHarvestSourceUri);
    }

    @Test
    public void createApiCatalog_WhenApiCatalogNotFound_ShouldCreateNew() {
        String orgNr = "testOrgNr";
        String testHarvestSourceUri = "testUri";
        ApiCatalog postPayload = ApiCatalog.builder().harvestSourceUri(testHarvestSourceUri).build();

        when(apiCatalogRepositoryMock.findByOrgNo(orgNr)).thenReturn(Optional.empty());
        when(apiCatalogRepositoryMock.save(any(ApiCatalog.class))).thenAnswer((invocation) -> invocation.getArguments()[0]);

        ApiCatalog apiCatalogSaved = apiCatalogController.createApiCatalog(orgNr, postPayload);

        assertEquals(apiCatalogSaved.getHarvestSourceUri(), testHarvestSourceUri);
        assertEquals(apiCatalogSaved.getOrgNo(), orgNr);
    }

    @Test
    public void deleteApiCatalog_WhenExisting_ShouldDelete() throws NotFoundException {
        ApiCatalog existingApiCatalog = new ApiCatalog();

        String orgNr = "testOrgNr";

        when(apiCatalogRepositoryMock.findByOrgNo(orgNr)).thenReturn(Optional.of(existingApiCatalog));

        apiCatalogController.deleteApiCatalog(orgNr);

        verify(apiCatalogRepositoryMock).delete(existingApiCatalog);
    }

    @Test
    public void deleteApiCatalog_WhenNotExisting_ShouldThrowNotFoundException() throws NotFoundException {
        String orgNr = "testOrgNr";

        when(apiCatalogRepositoryMock.findByOrgNo(orgNr)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> apiCatalogController.deleteApiCatalog(orgNr));

        verify(apiCatalogRepositoryMock, never()).delete(any());
    }
}
