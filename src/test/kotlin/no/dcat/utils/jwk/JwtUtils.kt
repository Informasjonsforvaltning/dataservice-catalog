package no.dcat.utils.jwk

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.*
import com.nimbusds.jose.jwk.gen.*
import java.util.UUID


object JwkStore{
    private val jwk = createJwk()

    private fun createJwk(): RSAKey{
        val  created = RSAKeyGenerator(2048)
                .algorithm(JWSAlgorithm.RS256)
                .keyUse(KeyUse.SIGNATURE)
                .keyID(UUID.randomUUID().toString())
                .generate()

        return created
    }

    fun get(): String {
        val token : JwkToken = jacksonObjectMapper()
                .readValue(jwk.toJSONString())
        return token.toString()
    }

    fun jwtHeader() = (
            JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(jwk.keyID)
                    .build()
            )

    fun signer() = (
            RSASSASigner(jwk)
            )

}

@JsonIgnoreProperties(ignoreUnknown = true)
class JwkToken(
        private val kid : String,
        private val kty :String,
        private val use : String,
        private val n : String,
        private val e : String
){

    override fun toString(): String {
        return  "{\n"+
                " \"keys\": [\n" +
                "   {\n" +
                "     \"kid\": \"$kid\",\n" +
                "     \"kty\": \"$kty\",\n" +
                "     \"alg\": \"RS256\",\n" +
                "     \"use\": \"$use\",\n" +
                "     \"n\": \"$n\",\n" +
                "     \"e\": \"$e\"\n" +
                "   }\n" +
                " ]\n" +
                "}"

    }
}
