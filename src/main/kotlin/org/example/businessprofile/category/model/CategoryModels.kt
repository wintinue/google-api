package org.example.businessprofile.category.model

import kotlinx.serialization.Serializable

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
