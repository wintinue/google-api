package org.example.routes

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import org.example.config.GoogleOAuthConfig
import org.example.config.appJson
import org.example.model.GoogleApiProxyRequest
import org.example.service.GoogleApiProxyService
import org.example.service.GoogleOAuthService

fun Application.googleApiModule(
    oauthService: GoogleOAuthService,
    apiProxyService: GoogleApiProxyService,
    config: GoogleOAuthConfig
) {
    install(ContentNegotiation) {
        json(appJson)
    }

    routing {
        get("/") {
            call.respond(
                buildJsonObject {
                    put("service", "google-api-oauth")
                    put("authorizeUrl", "${config.baseUrl}/api/v1/auth/google-api/authorize")
                    put("callbackUrl", config.redirectUri)
                    put("tokenStatusUrl", "${config.baseUrl}/api/v1/auth/google-api/token")
                    put("proxyUrl", "${config.baseUrl}/api/v1/google-api/call")
                }
            )
        }

        get("/api/v1/auth/google-api/authorize") {
            val requestedScopes = call.request.queryParameters["scopes"]
                ?.split(",")
                ?.map(String::trim)
                ?.filter(String::isNotBlank)
                ?: oauthService.defaultScopes()
            val auth = oauthService.buildAuthorization(requestedScopes)
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
                put("expiresAt", token.expiresAtEpochSeconds)
                put("scopes", appJson.encodeToJsonElement(token.scope))
            })
        }

        get("/api/v1/auth/google-api/token") {
            val token = oauthService.currentToken()
            if (token == null) {
                call.respond(buildJsonObject {
                    put("authorized", false)
                    put("authorizeUrl", "${config.baseUrl}/api/v1/auth/google-api/authorize")
                })
                return@get
            }

            call.respond(buildJsonObject {
                put("authorized", true)
                put("expiresAt", token.expiresAtEpochSeconds)
                put("hasRefreshToken", !token.refreshToken.isNullOrBlank())
                put("scopes", appJson.encodeToJsonElement(token.scope))
            })
        }

        post("/api/v1/google-api/call") {
            val request = call.receive<GoogleApiProxyRequest>()
            val response = apiProxyService.callApi(request)
            call.respond(io.ktor.http.HttpStatusCode.fromValue(response.statusCode), response.payload)
        }
    }
}
