package org.example.businessprofile.category.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.businessprofile.category.model.CategoryBatchGetQuery
import org.example.businessprofile.category.model.CategoryListQuery
import org.example.service.BusinessProfileService

fun Route.registerCategoryRoutes(
    businessProfileService: BusinessProfileService
) {
    get("/api/v1/business-profile/categories") {
        val regionCode = call.request.queryParameters["regionCode"]
        val languageCode = call.request.queryParameters["languageCode"]
        if (regionCode.isNullOrBlank() || languageCode.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                buildJsonObject {
                    put("error", "regionCode and languageCode are required")
                }
            )
            return@get
        }

        val request = CategoryListQuery(
            regionCode = regionCode,
            languageCode = languageCode,
            view = call.request.queryParameters["view"] ?: "BASIC",
            searchTerm = call.request.queryParameters["searchTerm"],
            pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull(),
            pageToken = call.request.queryParameters["pageToken"]
        )
        val response = businessProfileService.listCategories(request)
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }

    get("/api/v1/business-profile/categories/batch-get") {
        val names = call.request.queryParameters.getAll("names")
            ?: call.request.queryParameters["names"]
                ?.split(",")
                ?.map(String::trim)
                ?.filter(String::isNotBlank)
            ?: emptyList()
        val languageCode = call.request.queryParameters["languageCode"]
        if (names.isEmpty() || languageCode.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                buildJsonObject {
                    put("error", "names and languageCode are required")
                }
            )
            return@get
        }

        val request = CategoryBatchGetQuery(
            names = names,
            languageCode = languageCode,
            view = call.request.queryParameters["view"] ?: "FULL"
        )
        val response = businessProfileService.batchGetCategories(request)
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }
}
