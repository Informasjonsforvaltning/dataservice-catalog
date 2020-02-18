package no.dcat.contract

import com.jayway.jsonpath.matchers.JsonPathMatchers.isJson
import com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath
import no.dcat.utils.ApiTestContainer
import no.dcat.utils.WIREMOCK_TEST_HOST
import no.dcat.utils.apiAuthorizedRequest
import no.dcat.utils.jwk.Access
import no.dcat.utils.jwk.JwtToken
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("contract")
class ApiCatalogContract : ApiTestContainer() {

    @Test
    fun createCatalogue() {
        val orgNo = "910244132"
        val harvestURI = "$WIREMOCK_TEST_HOST/harvesturi"

        val response = apiAuthorizedRequest(
            "/catalogs/910244132/apicatalog",
            "{\"orgNo\": \"$orgNo\", \"harvestSourceUri\": \"$harvestURI\"}",
            JwtToken(Access.ORG_WRITE).toString(),
            "POST")

        assertEquals(HttpStatus.OK.value(), response["status"])
        MatcherAssert.assertThat(
            response["body"],
            isJson(
                CoreMatchers.allOf(
                    withJsonPath("$.id"),
                    withJsonPath("$.orgNo", CoreMatchers.equalTo(orgNo)),
                    withJsonPath("$.harvestSourceUri", CoreMatchers.equalTo(harvestURI)))))
    }

}