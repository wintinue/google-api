package org.example.merchant.product.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MerchantProductListQuery(
    val accountId: String,
    val pageSize: Int? = null,
    val pageToken: String? = null
)

@Serializable
data class MerchantProductGetQuery(
    val accountId: String,
    val productId: String
)

@Serializable
data class MerchantProductCreateRequest(
    val accountId: String,
    val dataSourceId: String,
    val productInput: JsonElement
)

@Serializable
data class MerchantProductUpdateRequest(
    val accountId: String,
    val dataSourceId: String,
    val productInputId: String,
    val updateMask: String? = null,
    val productInput: JsonElement
)

@Serializable
data class MerchantProductDeleteRequest(
    val accountId: String,
    val dataSourceId: String,
    val productInputId: String
)
