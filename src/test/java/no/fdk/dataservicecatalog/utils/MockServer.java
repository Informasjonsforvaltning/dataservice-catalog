package no.fdk.dataservicecatalog.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import no.fdk.dataservicecatalog.utils.jwk.JwkStore;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MockServer {
    private static WireMockServer mockserver = new WireMockServer(5000);

    public static void startMockServer() {
        if(!mockserver.isRunning()) {
            mockserver.stubFor(get(urlEqualTo("/ping"))
                    .willReturn(aResponse()
                            .withStatus(200))
            );
            mockserver.stubFor(get(urlEqualTo("/auth/realms/fdk/protocol/openid-connect/certs"))
                    .willReturn(okJson(JwkStore.get())));
            mockserver.start();
        }
    }

}
