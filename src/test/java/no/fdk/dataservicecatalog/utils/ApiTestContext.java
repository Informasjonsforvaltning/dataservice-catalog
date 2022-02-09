package no.fdk.dataservicecatalog.utils;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

import static no.fdk.dataservicecatalog.utils.MockServer.startMockServer;
import static no.fdk.dataservicecatalog.utils.TestData.MONGO_PORT;

public class ApiTestContext {
    public static GenericContainer mongoContainer;

    @LocalServerPort
    public Integer port = 0;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    Map.ofEntries(
                            Map.entry("spring.data.mongodb.authentication-database", "admin"),
                            Map.entry("spring.data.mongodb.database", TestData.MONGO_DB_NAME),
                            Map.entry("spring.data.mongodb.username", TestData.MONGO_USER),
                            Map.entry("spring.data.mongodb.password", TestData.MONGO_PASSWORD),
                            Map.entry("spring.data.mongodb.host", "localhost"),
                            Map.entry("spring.data.mongodb.port", mongoContainer.getMappedPort(MONGO_PORT).toString())
                    )
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    static {

        startMockServer();

        mongoContainer = new GenericContainer("mongo:latest")
                .withEnv(TestData.MONGO_ENV_VALUES)
                .withExposedPorts(TestData.MONGO_PORT)
                .waitingFor(Wait.forListeningPort());
        mongoContainer.start();

        TestUtils.resetDB();
    }

}
