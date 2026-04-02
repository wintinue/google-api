package org.example.businessprofile.location.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.businessprofile.location.model.BusinessProfileLocationDeleteRequest
import org.example.businessprofile.location.model.BusinessProfileLocationGetQuery
import org.example.businessprofile.location.model.BusinessProfileLocationListQuery
import org.example.businessprofile.location.model.BusinessProfileLocationUpdateRequest
import org.example.service.BusinessProfileService

fun Route.registerLocationRoutes(
    businessProfileService: BusinessProfileService
) {
    get("/api/v1/business-profile/locations") {
        val readMask = call.request.queryParameters["readMask"]
        if (readMask.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                buildJsonObject { put("error", "readMask is required") }
            )
            return@get
        }

        val locationId = call.request.queryParameters["locationId"]
        if (locationId.isNullOrBlank()) {
            val accountId = call.request.queryParameters["accountId"]
            if (accountId.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    buildJsonObject { put("error", "accountId and readMask are required") }
                )
                return@get
            }

            val response = businessProfileService.listLocations(
                BusinessProfileLocationListQuery(
                    accountId = accountId,
                    readMask = readMask,
                    pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull(),
                    pageToken = call.request.queryParameters["pageToken"],
                    filter = call.request.queryParameters["filter"],
                    orderBy = call.request.queryParameters["orderBy"]
                )
            )
            call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
            return@get
        }

        val response = businessProfileService.getLocation(
            BusinessProfileLocationGetQuery(
                locationId = locationId,
                readMask = readMask
            )
        )
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }

    patch("/api/v1/business-profile/locations") {
        val request = call.receive<BusinessProfileLocationUpdateRequest>()
        val response = businessProfileService.updateLocation(request)
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }

    delete("/api/v1/business-profile/locations") {
        val request = call.receive<BusinessProfileLocationDeleteRequest>()
        val response = businessProfileService.deleteLocation(request)
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }
}
