package org.example.service

import io.ktor.http.encodeURLParameter
import org.example.model.CategoryBatchGetQuery
import org.example.model.CategoryListQuery
import org.example.model.GoogleApiProxyRequest
import org.example.model.GoogleApiProxyResponse

class BusinessProfileService(
    private val apiProxyService: GoogleApiProxyService
) {
    suspend fun listCategories(request: CategoryListQuery): GoogleApiProxyResponse {
        val queryParams = buildList {
            add("regionCode=${request.regionCode.encodeURLParameter()}")
            add("languageCode=${request.languageCode.encodeURLParameter()}")
            add("view=${request.view.encodeURLParameter()}")
            request.searchTerm
                ?.takeIf { it.isNotBlank() }
                ?.let { add("filter=${"displayName=$it".encodeURLParameter()}") }
            request.pageSize?.let { add("pageSize=$it") }
            request.pageToken
                ?.takeIf { it.isNotBlank() }
                ?.let { add("pageToken=${it.encodeURLParameter()}") }
        }

        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://mybusinessbusinessinformation.googleapis.com/v1/categories?${queryParams.joinToString("&")}",
                method = "GET"
            )
        )
    }

    suspend fun batchGetCategories(request: CategoryBatchGetQuery): GoogleApiProxyResponse {
        require(request.names.isNotEmpty()) { "At least one category name is required" }

        val queryParams = buildList {
            add("languageCode=${request.languageCode.encodeURLParameter()}")
            add("view=${request.view.encodeURLParameter()}")
            request.names.forEach { add("names=${it.encodeURLParameter()}") }
        }

        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://mybusinessbusinessinformation.googleapis.com/v1/categories:batchGet?${queryParams.joinToString("&")}",
                method = "GET"
            )
        )
    }
}
