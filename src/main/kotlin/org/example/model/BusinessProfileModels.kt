package org.example.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CategoryListQuery(
    val regionCode: String,
    val languageCode: String,
    val view: String = "BASIC",
    val searchTerm: String? = null,
    val pageSize: Int? = null,
    val pageToken: String? = null
)

@Serializable
data class CategoryBatchGetQuery(
    val names: List<String>,
    val languageCode: String,
    val view: String = "FULL"
)

data class GoogleApiProxyResponse(
    val statusCode: Int,
    val payload: JsonElement
)
