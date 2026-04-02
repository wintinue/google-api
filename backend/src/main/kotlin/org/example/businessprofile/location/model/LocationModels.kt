package org.example.businessprofile.location.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BusinessProfileLocationListQuery(
    val accountId: String,
    val readMask: String,
    val pageSize: Int? = null,
    val pageToken: String? = null,
    val filter: String? = null,
    val orderBy: String? = null
)

@Serializable
data class BusinessProfileLocationGetQuery(
    val locationId: String,
    val readMask: String
)

@Serializable
data class BusinessProfileLocationUpdateRequest(
    val locationId: String,
    val updateMask: String,
    val validateOnly: Boolean? = null,
    val location: JsonElement
)

@Serializable
data class BusinessProfileLocationDeleteRequest(
    val locationId: String
)
