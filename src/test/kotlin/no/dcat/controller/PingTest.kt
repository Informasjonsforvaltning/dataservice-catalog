package no.dcat.controller

import no.dcat.utils.ApiTestContainer
import no.dcat.utils.apiGet
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("contract")
class PingTest : ApiTestContainer() {

    @Test
    fun pingTest() {
        val response = apiGet("/ping", "application/json")
        assertEquals(HttpStatus.OK.value(), response["status"])
    }

}