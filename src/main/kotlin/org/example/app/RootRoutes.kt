package org.example.app

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.put
import org.example.service.GoogleOAuthService

fun Route.registerRootRoutes(oauthService: GoogleOAuthService) {
    get("/") {
        val baseUrl = oauthService.baseUrl()
        val authorizeUrl = oauthService.authorizeUrl(null)
        call.respond(
            buildJsonObject {
                put("service", "google-api-oauth")
                put("defaultCredentialKey", oauthService.defaultCredentialKey())
                put("authorizeUrl", authorizeUrl)
                put("tokenStatusUrl", "$baseUrl/api/v1/auth/google-api/token")
                put("proxyUrl", "$baseUrl/api/v1/google-api/call")
                put("categoryListUrl", "$baseUrl/api/v1/business-profile/categories")
                put("categoryBatchGetUrl", "$baseUrl/api/v1/business-profile/categories/batch-get")
                put("merchantProductsUrl", "$baseUrl/api/v1/merchant/products")
                put("merchantProductInputsUrl", "$baseUrl/api/v1/merchant/product-inputs")
                put("swaggerUrl", "$baseUrl/swagger")
                putJsonArray("availableCredentialKeys") {
                    oauthService.availableCredentialKeys().forEach { add(JsonPrimitive(it)) }
                }
            }
        )
    }
}
