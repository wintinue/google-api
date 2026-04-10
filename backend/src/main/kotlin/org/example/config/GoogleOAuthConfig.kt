package org.example.config

import java.io.File
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class GoogleOAuthConfig(
    val credentialKey: String,
    val clientId: String,
    val clientSecret: String,
    val authUri: String,
    val tokenUri: String,
    val redirectUri: String,
    val baseUrl: String
)

class GoogleOAuthConfigProvider(
    val baseUrl: String,
    private val credentialsByKey: Map<String, GoogleOAuthClientSecret>,
    private val defaultCredentialKey: String
) {
    fun resolve(credentialKey: String?): GoogleOAuthConfig {
        val resolvedKey = credentialKey?.takeIf { it.isNotBlank() } ?: defaultCredentialKey
        val secret = credentialsByKey[resolvedKey]
            ?: error(
                "OAuth credential key not found: $resolvedKey. Available keys: ${
                    credentialsByKey.keys.sorted().joinToString(", ")
                }"
            )
        val redirectUri = System.getenv("GOOGLE_OAUTH_REDIRECT_URI")
            ?: "$baseUrl/api/v1/auth/google-api/redirection"

        return GoogleOAuthConfig(
            credentialKey = resolvedKey,
            clientId = secret.clientId,
            clientSecret = secret.clientSecret,
            authUri = secret.authUri,
            tokenUri = secret.tokenUri,
            redirectUri = redirectUri,
            baseUrl = baseUrl
        )
    }

    fun defaultCredentialKey(): String = defaultCredentialKey

    fun availableCredentialKeys(): List<String> = credentialsByKey.keys.sorted()

    companion object {
        fun load(): GoogleOAuthConfigProvider {
            val baseUrl = System.getenv("APP_BASE_URL") ?: "http://localhost:8088"
            val credentialPath = System.getenv("GOOGLE_OAUTH_CLIENT_SECRET_PATH")
                ?: "credentials/google-oauth-client.json"
            val defaultCredentialKey = System.getenv("GOOGLE_OAUTH_CLIENT_KEY") ?: "default"
            val credentialFile = File(credentialPath)
            require(credentialFile.exists()) {
                "OAuth credential file not found: ${credentialFile.absolutePath}"
            }

            val rootObject = appJson.parseToJsonElement(credentialFile.readText()).jsonObject
            val credentialsByKey = rootObject
                .mapValues { (_, value) ->
                    appJson.decodeFromJsonElement(GoogleOAuthClientSecret.serializer(), value)
                }

            require(credentialsByKey.isNotEmpty()) {
                "OAuth credential file does not contain any credential objects: ${credentialFile.absolutePath}"
            }
            require(defaultCredentialKey in credentialsByKey) {
                "Default OAuth credential key not found: $defaultCredentialKey. Available keys: ${
                    credentialsByKey.keys.sorted().joinToString(", ")
                }"
            }

            return GoogleOAuthConfigProvider(
                baseUrl = baseUrl,
                credentialsByKey = credentialsByKey,
                defaultCredentialKey = defaultCredentialKey
            )
        }
    }
}

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
