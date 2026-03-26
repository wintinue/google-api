package org.example.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import java.net.URLEncoder
import java.time.Instant
import java.util.UUID
import org.example.config.GoogleOAuthConfigProvider
import org.example.config.appJson
import org.example.oauth.model.AuthorizationRequest
import org.example.oauth.model.GoogleTokenResponse
import org.example.oauth.model.StoredToken
import org.example.oauth.store.TokenStore

class GoogleOAuthService(
    private val configProvider: GoogleOAuthConfigProvider,
    private val tokenStore: TokenStore
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

    fun defaultScopes(): List<String> = listOf(
        "openid",
        "https://www.googleapis.com/auth/userinfo.email",
        "https://www.googleapis.com/auth/userinfo.profile",
        "https://www.googleapis.com/auth/business.manage",
        "https://www.googleapis.com/auth/content"
    )

    fun buildAuthorization(scopes: List<String>, credentialKey: String?): AuthorizationRequest {
        val config = configProvider.resolve(credentialKey)
        val state = UUID.randomUUID().toString()
        tokenStore.savePendingState(state, scopes, config.credentialKey)

        val params = linkedMapOf(
            "client_id" to config.clientId,
            "redirect_uri" to config.redirectUri,
            "response_type" to "code",
            "access_type" to "offline",
            "include_granted_scopes" to "true",
            "prompt" to "consent",
            "scope" to scopes.joinToString(" "),
            "state" to state
        )
        val url = config.authUri + "?" + params.entries.joinToString("&") {
            "${it.key}=${URLEncoder.encode(it.value, Charsets.UTF_8)}"
        }
        return AuthorizationRequest(url, state)
    }

    suspend fun exchangeCode(code: String, state: String): StoredToken {
        val pendingState = tokenStore.consumePendingState(state)
            ?: error("Invalid or expired OAuth state")
        val config = configProvider.resolve(pendingState.credentialKey)
        val tokenResponse = httpClient.submitForm(
            url = config.tokenUri,
            formParameters = Parameters.build {
                append("code", code)
                append("client_id", config.clientId)
                append("client_secret", config.clientSecret)
                append("redirect_uri", config.redirectUri)
                append("grant_type", "authorization_code")
            }
        ).body<GoogleTokenResponse>()

        return tokenStore.saveToken(
            StoredToken(
                accessToken = tokenResponse.accessToken,
                refreshToken = tokenResponse.refreshToken,
                expiresAtEpochSeconds = Instant.now().epochSecond + tokenResponse.expiresIn,
                scope = tokenResponse.scope?.split(" ") ?: pendingState.scopes,
                tokenType = tokenResponse.tokenType,
                credentialKey = pendingState.credentialKey
            )
        )
    }

    fun currentToken(): StoredToken? = tokenStore.readToken()

    suspend fun ensureValidToken(): StoredToken {
        val current = tokenStore.readToken() ?: error("Google OAuth token not found. Authorize first.")
        if (current.expiresAtEpochSeconds > Instant.now().epochSecond + 30) {
            return current
        }

        val refreshToken = current.refreshToken ?: error("Refresh token missing. Re-authorize with prompt=consent.")
        val config = configProvider.resolve(current.credentialKey)
        val refreshed = httpClient.submitForm(
            url = config.tokenUri,
            formParameters = Parameters.build {
                append("client_id", config.clientId)
                append("client_secret", config.clientSecret)
                append("refresh_token", refreshToken)
                append("grant_type", "refresh_token")
            }
        ).body<GoogleTokenResponse>()

        return tokenStore.saveToken(
            current.copy(
                accessToken = refreshed.accessToken,
                expiresAtEpochSeconds = Instant.now().epochSecond + refreshed.expiresIn,
                tokenType = refreshed.tokenType,
                scope = refreshed.scope?.split(" ") ?: current.scope,
                refreshToken = refreshed.refreshToken ?: current.refreshToken,
                credentialKey = current.credentialKey
            )
        )
    }

    fun defaultCredentialKey(): String = configProvider.defaultCredentialKey()

    fun availableCredentialKeys(): List<String> = configProvider.availableCredentialKeys()

    fun baseUrl(): String = configProvider.baseUrl

    fun authorizeUrl(credentialKey: String?): String {
        val resolvedKey = credentialKey?.takeIf { it.isNotBlank() } ?: defaultCredentialKey()
        return "${configProvider.baseUrl}/api/v1/auth/google-api/authorize?credentialKey=$resolvedKey"
    }
}
