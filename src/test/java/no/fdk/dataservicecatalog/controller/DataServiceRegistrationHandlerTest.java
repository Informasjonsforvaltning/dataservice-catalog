package no.fdk.dataservicecatalog.controller;

import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import no.fdk.dataservicecatalog.security.AuthenticationManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class DataServiceRegistrationHandlerTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private DataServiceMongoRepository dataServiceMongoRepository;

    @Autowired
    private WebTestClient webTestClient;

    private void mockAuthority(String role) {
        var authority = new SimpleGrantedAuthority(role);
        var authentication = new OpenIDAuthenticationToken("", Collections.singletonList(authority), "", Collections.emptyList());
        when(authenticationManager.authenticate(any())).thenReturn(Mono.just(authentication));
    }

    private DataService withUUID(DataService dataService) {
        dataService.setId(UUID.randomUUID().toString());
        return dataService;
    }

    @Test
    void getTwoDataServicesByCatalogId() {
        mockAuthority("organization:910258028:admin");

        var first = DataService.builder().title(Collections.singletonMap(DataService.DEFAULT_LANGUAGE, "first")).build();
        var second = DataService.builder().title(Collections.singletonMap(DataService.DEFAULT_LANGUAGE, "second")).build();

        when(dataServiceMongoRepository.findAllByCatalogId(any())).thenReturn(Flux.just(first, second));

        webTestClient.get().uri("/catalogs/910258028/dataservices")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                .exchange().expectStatus().isOk()
                .expectBodyList(DataService.class).hasSize(2).contains(first, second);
    }

    @Test
    void createNewDataService() {
        mockAuthority("organization:910258028:admin");

        var expected = DataService.builder().title(Collections.singletonMap(DataService.DEFAULT_LANGUAGE, "expected")).build();
        var locationPattern = "\\/catalogs\\/910258028\\/dataservices\\/[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}";

        when(dataServiceMongoRepository.save(any())).thenReturn(Mono.just(withUUID(expected)));

        webTestClient.patch().uri("/catalogs/910258028/dataservices")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(expected)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("location", locationPattern);

    }

    @Test
    void failOnPermission() {
        mockAuthority("organization:910258028:read");
        webTestClient.delete().uri("/catalogs/910258028/dataservices/11111")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                .exchange()
                .expectStatus().isForbidden();
    }

}
