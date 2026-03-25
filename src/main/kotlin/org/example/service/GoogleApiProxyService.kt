package org.example.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.config.appJson
import org.example.model.GoogleApiProxyRequest
import org.example.model.GoogleApiProxyResponse

class GoogleApiProxyService(
    private val oauthService: GoogleOAuthService
) {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(appJson)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    suspend fun callApi(request: GoogleApiProxyRequest): GoogleApiProxyResponse {
        val token = oauthService.ensureValidToken()
        val method = HttpMethod.parse(request.method.uppercase())
        val response = httpClient.prepareRequest(request.url) {
            this.method = method
            headers {
                append(HttpHeaders.Authorization, "Bearer ${token.accessToken}")
                request.headers?.forEach { (key, value) ->
                    append(key, value)
                }
            }
            if (request.body != null) {
                contentType(ContentType.Application.Json)
                setBody(request.body)
            }
        }.execute()

        val rawBody = response.bodyAsText()
        val payload = runCatching { appJson.parseToJsonElement(rawBody) }.getOrElse {
            buildJsonObject {
                put("raw", rawBody)
            }
        }

        return GoogleApiProxyResponse(response.status.value, payload)
    }
}
