package org.example.merchant.account.model

import kotlinx.serialization.Serializable

@Serializable
data class MerchantAccountListQuery(
    val pageSize: Int? = null,
    val pageToken: String? = null,
    val filter: String? = null
)
