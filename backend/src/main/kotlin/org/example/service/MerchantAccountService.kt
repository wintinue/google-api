package org.example.service

import io.ktor.http.encodeURLQueryComponent
import org.example.googleapi.model.GoogleApiProxyRequest
import org.example.googleapi.model.GoogleApiProxyResponse
import org.example.merchant.account.model.MerchantAccountListQuery

class MerchantAccountService(
    private val apiProxyService: GoogleApiProxyService
) {
    suspend fun listAccounts(request: MerchantAccountListQuery): GoogleApiProxyResponse {
        val queryParams = buildList {
            request.pageSize?.let { add("pageSize=$it") }
            request.pageToken
                ?.takeIf { it.isNotBlank() }
                ?.let { add("pageToken=${it.encodeURLQueryComponent()}") }
            request.filter
                ?.takeIf { it.isNotBlank() }
                ?.let { add("filter=${it.encodeURLQueryComponent()}") }
        }
        val querySuffix = if (queryParams.isEmpty()) "" else "?${queryParams.joinToString("&")}"

        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://merchantapi.googleapis.com/accounts/v1beta/accounts$querySuffix",
                method = "GET"
            )
        )
    }
}
