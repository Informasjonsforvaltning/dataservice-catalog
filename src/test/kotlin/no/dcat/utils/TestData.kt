package no.dcat.utils

import no.dcat.utils.ApiTestContainer.Companion.TEST_API
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap

const val API_PORT = 8080
const val LOCAL_SERVER_PORT = 5000

const val ELASTIC_PORT = 9200
const val ELASTIC_TCP_PORT = 9300
const val ELASTIC_NETWORK_NAME = "elasticsearch5"
const val ELASTIC_CLUSERNAME = "elasticsearch"
const val ELASTIC_CLUSTERNODES = "$ELASTIC_NETWORK_NAME:$ELASTIC_TCP_PORT"

const val WIREMOCK_TEST_HOST = "http://host.testcontainers.internal:$LOCAL_SERVER_PORT"

val API_ENV_VALUES : Map<String,String> = ImmutableMap.of(
    "SPRING_PROFILES_ACTIVE" , "test",
    "WIREMOCK_TEST_HOST" , WIREMOCK_TEST_HOST,
    "FDK_ES_CLUSTERNODES" , ELASTIC_CLUSTERNODES,
    "FDK_ES_CLUSTERNAME" , ELASTIC_CLUSERNAME,
    "API_REGISTRATIONS_FILENAME" , "test-apis"
)
val ELASTIC_ENV_VALUES : Map<String,String> = ImmutableMap.of(
    "cluster.name" , ELASTIC_CLUSERNAME,
    "xpack.security.enabled", "false",
    "xpack.monitoring.enabled", "false"
)

fun getApiAddress( endpoint: String ): String{
    return "http://${TEST_API.getContainerIpAddress()}:${TEST_API.getMappedPort(API_PORT)}$endpoint"
}