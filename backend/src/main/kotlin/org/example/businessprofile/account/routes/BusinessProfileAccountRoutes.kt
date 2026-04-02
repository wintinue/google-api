package org.example.businessprofile.account.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.example.businessprofile.account.model.BusinessProfileAccountListQuery
import org.example.service.BusinessProfileService

fun Route.registerBusinessProfileAccountRoutes(
    businessProfileService: BusinessProfileService
) {
    get("/api/v1/business-profile/accounts") {
        val response = businessProfileService.listAccounts(
            BusinessProfileAccountListQuery(
                parentAccount = call.request.queryParameters["parentAccount"],
                pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull(),
                pageToken = call.request.queryParameters["pageToken"]
            )
        )
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }
}
