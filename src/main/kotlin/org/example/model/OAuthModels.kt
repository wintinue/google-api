package org.example.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

data class AuthorizationRequest(val url: String, val state: String)

@Serializable
data class PendingState(
    val state: String,
    val scopes: List<String>,
    val createdAtEpochSeconds: Long
)

@Serializable
data class StoredToken(
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresAtEpochSeconds: Long,
    val scope: List<String> = emptyList(),
    val tokenType: String = "Bearer"
)

@Serializable
data class GoogleTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    val scope: String? = null,
    @SerialName("token_type")
    val tokenType: String = "Bearer"
)

@Serializable
data class GoogleApiProxyRequest(
    val url: String,
    val method: String = "GET",
    val headers: Map<String, String>? = null,
    val body: JsonElement? = null
)
