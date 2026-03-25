package org.example.config

import java.io.File
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class GoogleOAuthConfig(
    val clientId: String,
    val clientSecret: String,
    val authUri: String,
    val tokenUri: String,
    val redirectUri: String,
    val baseUrl: String
) {
    companion object {
        fun load(): GoogleOAuthConfig {
            val baseUrl = System.getenv("APP_BASE_URL") ?: "http://localhost:8080"
            val credentialPath = System.getenv("GOOGLE_OAUTH_CLIENT_SECRET_PATH")
                ?: "credentials/google-oauth-client.json"
            val credentialFile = File(credentialPath)
            require(credentialFile.exists()) {
                "OAuth credential file not found: ${credentialFile.absolutePath}"
            }

            val secretFile = appJson.decodeFromString<GoogleOAuthClientSecretFile>(credentialFile.readText())
            val redirectUri = System.getenv("GOOGLE_OAUTH_REDIRECT_URI")
                ?: secretFile.web.redirectUris.firstOrNull()
                ?: "$baseUrl/api/v1/auth/google-api/redirection"

            return GoogleOAuthConfig(
                clientId = secretFile.web.clientId,
                clientSecret = secretFile.web.clientSecret,
                authUri = secretFile.web.authUri,
                tokenUri = secretFile.web.tokenUri,
                redirectUri = redirectUri,
                baseUrl = baseUrl
            )
        }
    }
}

@Serializable
data class GoogleOAuthClientSecretFile(
    val web: GoogleOAuthClientSecret
)

@Serializable
data class GoogleOAuthClientSecret(
    @SerialName("client_id")
    val clientId: String,
    @SerialName("client_secret")
    val clientSecret: String,
    @SerialName("auth_uri")
    val authUri: String,
    @SerialName("token_uri")
    val tokenUri: String,
    @SerialName("redirect_uris")
    val redirectUris: List<String>
)
