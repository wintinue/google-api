package org.example.service

import io.ktor.http.encodeURLPathPart
import io.ktor.http.encodeURLParameter
import io.ktor.http.encodeURLQueryComponent
import org.example.businessprofile.account.model.BusinessProfileAccountListQuery
import org.example.businessprofile.category.model.CategoryBatchGetQuery
import org.example.businessprofile.category.model.CategoryListQuery
import org.example.businessprofile.location.model.BusinessProfileLocationDeleteRequest
import org.example.businessprofile.location.model.BusinessProfileLocationGetQuery
import org.example.businessprofile.location.model.BusinessProfileLocationListQuery
import org.example.businessprofile.location.model.BusinessProfileLocationUpdateRequest
import org.example.googleapi.model.GoogleApiProxyRequest
import org.example.googleapi.model.GoogleApiProxyResponse

class BusinessProfileService(
    private val apiProxyService: GoogleApiProxyService
) {
    suspend fun listAccounts(request: BusinessProfileAccountListQuery): GoogleApiProxyResponse {
        val queryParams = buildList {
            request.parentAccount
                ?.takeIf { it.isNotBlank() }
                ?.let { add("parentAccount=${normalizeAccountName(it).encodeURLQueryComponent()}") }
            request.pageSize?.let { add("pageSize=$it") }
            request.pageToken
                ?.takeIf { it.isNotBlank() }
                ?.let { add("pageToken=${it.encodeURLQueryComponent()}") }
        }
        val querySuffix = if (queryParams.isEmpty()) "" else "?${queryParams.joinToString("&")}"

        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://mybusinessaccountmanagement.googleapis.com/v1/accounts$querySuffix",
                method = "GET"
            )
        )
    }

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

    suspend fun listLocations(request: BusinessProfileLocationListQuery): GoogleApiProxyResponse {
        val queryParams = buildList {
            add("readMask=${request.readMask.encodeURLQueryComponent()}")
            request.pageSize?.let { add("pageSize=$it") }
            request.pageToken
                ?.takeIf { it.isNotBlank() }
                ?.let { add("pageToken=${it.encodeURLQueryComponent()}") }
            request.filter
                ?.takeIf { it.isNotBlank() }
                ?.let { add("filter=${it.encodeURLQueryComponent()}") }
            request.orderBy
                ?.takeIf { it.isNotBlank() }
                ?.let { add("orderBy=${it.encodeURLQueryComponent()}") }
        }

        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://mybusinessbusinessinformation.googleapis.com/v1/${buildAccountPath(request.accountId)}/locations?${queryParams.joinToString("&")}",
                method = "GET"
            )
        )
    }

    suspend fun getLocation(request: BusinessProfileLocationGetQuery): GoogleApiProxyResponse {
        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://mybusinessbusinessinformation.googleapis.com/v1/${buildLocationPath(request.locationId)}?readMask=${request.readMask.encodeURLQueryComponent()}",
                method = "GET"
            )
        )
    }

    suspend fun updateLocation(request: BusinessProfileLocationUpdateRequest): GoogleApiProxyResponse {
        val queryParams = buildList {
            add("updateMask=${request.updateMask.encodeURLQueryComponent()}")
            request.validateOnly?.let { add("validateOnly=$it") }
        }

        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://mybusinessbusinessinformation.googleapis.com/v1/${buildLocationPath(request.locationId)}?${queryParams.joinToString("&")}",
                method = "PATCH",
                body = request.location
            )
        )
    }

    suspend fun deleteLocation(request: BusinessProfileLocationDeleteRequest): GoogleApiProxyResponse {
        return apiProxyService.callApi(
            GoogleApiProxyRequest(
                url = "https://mybusinessbusinessinformation.googleapis.com/v1/${buildLocationPath(request.locationId)}",
                method = "DELETE"
            )
        )
    }

    private fun buildAccountPath(accountId: String): String {
        val normalizedAccountId = accountId.removePrefix("accounts/")
        return "accounts/${normalizedAccountId.encodeURLPathPart()}"
    }

    private fun normalizeAccountName(accountId: String): String {
        val normalizedAccountId = accountId.removePrefix("accounts/")
        return "accounts/$normalizedAccountId"
    }

    private fun buildLocationPath(locationId: String): String {
        val normalizedLocationId = locationId.removePrefix("locations/")
        return "locations/${normalizedLocationId.encodeURLPathPart()}"
    }
}
