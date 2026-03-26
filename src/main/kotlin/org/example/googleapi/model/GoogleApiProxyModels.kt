package org.example.googleapi.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GoogleApiProxyRequest(
    val url: String,
    val method: String = "GET",
    val headers: Map<String, String>? = null,
    val body: JsonElement? = null
)

data class GoogleApiProxyResponse(
    val statusCode: Int,
    val payload: JsonElement
)
