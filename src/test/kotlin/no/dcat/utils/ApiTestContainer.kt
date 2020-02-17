package no.dcat.utils

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.testcontainers.Testcontainers
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import java.io.IOException
import java.time.Duration

abstract class ApiTestContainer {
    companion object {

        private val logger = LoggerFactory.getLogger(ApiTestContainer::class.java)
        private val apiLog = Slf4jLogConsumer(logger).withPrefix("API-LOG")
        var elasticContainer: KGenericContainer
        var TEST_API: KGenericContainer

        init {

            startMockServer()

            Testcontainers.exposeHostPorts(LOCAL_SERVER_PORT)
            val apiNetwork = Network.newNetwork()

            elasticContainer = KGenericContainer("docker.elastic.co/elasticsearch/elasticsearch:5.6.9")
                .withEnv(ELASTIC_ENV_VALUES)
                .withExposedPorts(ELASTIC_PORT, ELASTIC_TCP_PORT)
                .waitingFor(HttpWaitStrategy()
                    .forPort(ELASTIC_PORT)
                    .forPath("/_cluster/health?pretty=true")
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofMinutes(1)))
                .withNetwork(apiNetwork)
                .withNetworkAliases(ELASTIC_NETWORK_NAME)

            TEST_API = KGenericContainer(System.getProperty("testImageName") ?: "eu.gcr.io/fdk-infra/api-catalogue:latest")
                .withExposedPorts(API_PORT)
                .dependsOn(elasticContainer)
                .waitingFor(HttpWaitStrategy()
                    .forPort(API_PORT)
                    .forPath("/ping")
                    .forStatusCode(HttpStatus.OK.value())
                    .withStartupTimeout(Duration.ofMinutes(1)))
                .withNetwork(apiNetwork)
                .withLogConsumer(apiLog)
                .withEnv(API_ENV_VALUES)

            elasticContainer.start()
            TEST_API.start()


            try {
                val result = TEST_API.execInContainer("wget", "-O", "-", "$WIREMOCK_TEST_HOST/ping")
                if (!result.stderr.contains("200")) {
                    logger.debug("Ping to mock server failed")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

}

// Hack needed because testcontainers use of generics confuses Kotlin
class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
