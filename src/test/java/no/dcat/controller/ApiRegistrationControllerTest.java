package no.dcat.controller;

import com.google.gson.Gson;
import no.dcat.model.ApiRegistration;
import no.dcat.model.Catalog;
import no.dcat.service.ApiCatService;
import no.dcat.service.ApiRegistrationRepository;
import no.dcat.service.CatalogRepository;
import no.dcat.service.InformationmodelCatService;
import no.fdk.acat.common.model.apispecification.ApiSpecification;
import no.fdk.webutils.exceptions.BadRequestException;
import no.fdk.webutils.exceptions.NotFoundException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static no.dcat.model.ApiRegistration.REGISTRATION_STATUS_DRAFT;
import static no.dcat.model.ApiRegistration.REGISTRATION_STATUS_PUBLISH;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class ApiRegistrationControllerTest {

    @InjectMocks
    private ApiRegistrationController apiRegistrationController;

    @Mock
    private CatalogRepository catalogRepositoryMock;

    @Mock
    private ApiRegistrationRepository apiRegistrationRepositoryMock;

    @Mock
    private ApiCatService apiCatMock;

    @Mock
    private InformationmodelCatService informationmodelCatMock;

    @Test
    public void createApiRegistrationOK() throws Throwable {
        Catalog catalog = new Catalog();
        String catalogId = "1234";
        catalog.setId(catalogId);
        when(catalogRepositoryMock.findById(anyString())).thenReturn(Optional.of(catalog));

        Resource apiResource = new ClassPathResource("raw-enhet-api.json");
        ApiSpecification apiSpecification =
            new Gson().fromJson(IOUtils.toString(apiResource.getInputStream(), StandardCharsets.UTF_8), ApiSpecification.class);
        when(apiCatMock.convertSpecUrlToApiSpecification(apiResource.getURL().toString())).thenReturn(apiSpecification);

        when(apiRegistrationRepositoryMock.save(any(ApiRegistration.class))).thenAnswer((invocation) -> invocation.getArguments()[0]);


        Map<String, Object> apiRegData = new HashMap<String, Object>() {{
            put("apiSpecUrl", apiResource.getURL().toString());
        }};

        ApiRegistration saved = apiRegistrationController.createApiRegistration(catalogId, apiRegData);

        assertThat(
            saved.getApiSpecification().getInfo().getTitle(),
            is("Åpne Data fra Enhetsregisteret - API Dokumentasjon"));
    }

    @Test
    public void checkIfCatalogIdNotMatchWillFailWithNotFound() throws NotFoundException, BadRequestException {

        String catalogId = "1234";
        String id = "1234";

        ApiRegistration apiRegistration = new ApiRegistration();
        apiRegistration.setCatalogId(catalogId + "0000");

        when(apiRegistrationRepositoryMock.findById(anyString())).thenReturn(Optional.of(apiRegistration));

        assertThrows(NotFoundException.class, () -> apiRegistrationController.deleteApiRegistration(catalogId, id));
    }

    @Test
    public void checkDeleteApiRegistrationWithStatusPublishedBadRequest() {

        String catalogId = "1234";
        String id = "1234";

        ApiRegistration apiRegistration = new ApiRegistration();
        apiRegistration.setCatalogId(catalogId);
        apiRegistration.setRegistrationStatus(REGISTRATION_STATUS_PUBLISH);

        when(apiRegistrationRepositoryMock.findById(id)).thenReturn(Optional.of(apiRegistration));

        assertThrows(BadRequestException.class, () -> apiRegistrationController.deleteApiRegistration(catalogId, id));
    }

    @Test
    public void checkDeleteApiRegistrationWithStatusDraftOK()
        throws NotFoundException, BadRequestException {

        String catalogId = "1234";
        String id = "1234";

        ApiRegistration apiRegistration = new ApiRegistration();
        apiRegistration.setCatalogId(catalogId);
        apiRegistration.setRegistrationStatus(REGISTRATION_STATUS_DRAFT);

        when(apiRegistrationRepositoryMock.findById(id)).thenReturn(Optional.of(apiRegistration));

        apiRegistrationController.deleteApiRegistration(catalogId, id);

        verify(apiRegistrationRepositoryMock, times(1)).delete(apiRegistration);
        verify(apiCatMock, times(1)).triggerHarvestApiRegistration(id);
    }
}
