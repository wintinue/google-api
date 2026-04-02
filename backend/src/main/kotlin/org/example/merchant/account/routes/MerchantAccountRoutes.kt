package org.example.merchant.account.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.example.merchant.account.model.MerchantAccountListQuery
import org.example.service.MerchantAccountService

fun Route.registerMerchantAccountRoutes(
    merchantAccountService: MerchantAccountService
) {
    get("/api/v1/merchant/accounts") {
        val response = merchantAccountService.listAccounts(
            MerchantAccountListQuery(
                pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull(),
                pageToken = call.request.queryParameters["pageToken"],
                filter = call.request.queryParameters["filter"]
            )
        )
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }
}
