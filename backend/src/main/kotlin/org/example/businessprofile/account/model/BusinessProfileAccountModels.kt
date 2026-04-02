package org.example.businessprofile.account.model

import kotlinx.serialization.Serializable

@Serializable
data class BusinessProfileAccountListQuery(
    val parentAccount: String? = null,
    val pageSize: Int? = null,
    val pageToken: String? = null
)
