package org.example.oauth.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.put
import org.example.config.appJson
import org.example.service.GoogleOAuthService

fun Route.registerOAuthRoutes(
    oauthService: GoogleOAuthService
) {
    get("/api/v1/auth/google-api/authorize") {
        val requestedScopes = call.request.queryParameters["scopes"]
            ?.split(",")
            ?.map(String::trim)
            ?.filter(String::isNotBlank)
            ?: oauthService.defaultScopes()
        val credentialKey = call.request.queryParameters["credentialKey"]
        val auth = oauthService.buildAuthorization(requestedScopes, credentialKey)
        call.respondRedirect(auth.url)
    }

    get("/api/v1/auth/google-api/redirection") {
        val code = call.request.queryParameters["code"]
        val state = call.request.queryParameters["state"]
        val error = call.request.queryParameters["error"]

        if (error != null) {
            call.respond(buildJsonObject {
                put("ok", false)
                put("error", error)
            })
            return@get
        }

        if (code.isNullOrBlank() || state.isNullOrBlank()) {
            call.respond(buildJsonObject {
                put("ok", false)
                put("error", "Missing code or state")
            })
            return@get
        }

        val token = oauthService.exchangeCode(code, state)
        call.respond(buildJsonObject {
            put("ok", true)
            put("message", "OAuth token stored successfully")
            put("credentialKey", token.credentialKey)
            put("expiresAt", token.expiresAtEpochSeconds)
            put("scopes", appJson.encodeToJsonElement(token.scope))
        })
    }

    get("/api/v1/auth/google-api/token") {
        val token = oauthService.currentToken()
        val credentialKey = call.request.queryParameters["credentialKey"]
        if (token == null) {
            call.respond(buildJsonObject {
                put("authorized", false)
                put("defaultCredentialKey", oauthService.defaultCredentialKey())
                put("authorizeUrl", oauthService.authorizeUrl(credentialKey))
                putJsonArray("availableCredentialKeys") {
                    oauthService.availableCredentialKeys().forEach { add(JsonPrimitive(it)) }
                }
            })
            return@get
        }

        call.respond(buildJsonObject {
            put("authorized", true)
            put("accessToken", token.accessToken)
            put("credentialKey", token.credentialKey)
            put("expiresAt", token.expiresAtEpochSeconds)
            put("hasRefreshToken", !token.refreshToken.isNullOrBlank())
            put("scopes", appJson.encodeToJsonElement(token.scope))
        })
    }
}
