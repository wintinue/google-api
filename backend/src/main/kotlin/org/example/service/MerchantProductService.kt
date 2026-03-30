package org.example.service

import io.ktor.http.encodeURLPathPart
import io.ktor.http.encodeURLQueryComponent
import java.util.Base64
import org.example.googleapi.model.GoogleApiProxyRequest
import org.example.googleapi.model.GoogleApiProxyResponse
import org.example.merchant.product.model.MerchantProductCreateRequest
import org.example.merchant.product.model.MerchantProductDeleteRequest
import org.example.merchant.product.model.MerchantProductGetQuery
import org.example.merchant.product.model.MerchantProductListQuery
import org.example.merchant.product.model.MerchantProductUpdateRequest

class MerchantProductService(
    private val apiProxyService: GoogleApiProxyService
) {
    suspend fun listProducts(request: MerchantProductListQuery): GoogleApiProxyResponse {
        val queryParams = buildList {
            request.pageSize?.let { add("pageSize=$it") }
            request.pageToken
                ?.takeIf { it.isNotBlank() }
                ?.let { add("pageToken=${it.encodeURLQueryComponent()}") }
        }
        val querySuffix = if (queryParams.isEmpty()) "" else "?${queryParams.joinToString("&")}"

        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://merchantapi.googleapis.com/products/v1/accounts/${request.accountId.encodeURLPathPart()}/products$querySuffix",
                method = "GET"
            )
        )
    }

    suspend fun getProduct(request: MerchantProductGetQuery): GoogleApiProxyResponse {
        val encodedProductId = encodeMerchantProductId(request.productId)
        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://merchantapi.googleapis.com/products/v1/accounts/${request.accountId.encodeURLPathPart()}/products/${encodedProductId.encodeURLPathPart()}",
                method = "GET"
            )
        )
    }

    suspend fun createProduct(request: MerchantProductCreateRequest): GoogleApiProxyResponse {
        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://merchantapi.googleapis.com/products/v1/accounts/${request.accountId.encodeURLPathPart()}/productInputs:insert?dataSource=${buildDataSourceName(request.accountId, request.dataSourceId).encodeURLQueryComponent()}",
                method = "POST",
                body = request.productInput
            )
        )
    }

    suspend fun updateProduct(request: MerchantProductUpdateRequest): GoogleApiProxyResponse {
        val queryParams = buildList {
            add("dataSource=${buildDataSourceName(request.accountId, request.dataSourceId).encodeURLQueryComponent()}")
            request.updateMask
                ?.takeIf { it.isNotBlank() }
                ?.let { add("updateMask=${it.encodeURLQueryComponent()}") }
        }
        val encodedProductInputId = encodeMerchantProductId(request.productInputId)

        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://merchantapi.googleapis.com/products/v1/accounts/${request.accountId.encodeURLPathPart()}/productInputs/${encodedProductInputId.encodeURLPathPart()}?${queryParams.joinToString("&")}",
                method = "PATCH",
                body = request.productInput
            )
        )
    }

    suspend fun deleteProduct(request: MerchantProductDeleteRequest): GoogleApiProxyResponse {
        val encodedProductInputId = encodeMerchantProductId(request.productInputId)
        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://merchantapi.googleapis.com/products/v1/accounts/${request.accountId.encodeURLPathPart()}/productInputs/${encodedProductInputId.encodeURLPathPart()}?dataSource=${buildDataSourceName(request.accountId, request.dataSourceId).encodeURLQueryComponent()}",
                method = "DELETE"
            )
        )
    }

    private fun buildDataSourceName(accountId: String, dataSourceId: String): String {
        return "accounts/$accountId/dataSources/$dataSourceId"
    }

    private fun encodeMerchantProductId(productId: String): String {
        return if (productId.contains("/") || productId.contains("%")) {
            Base64.getUrlEncoder().withoutPadding().encodeToString(productId.toByteArray())
        } else {
            productId
        }
    }
}
