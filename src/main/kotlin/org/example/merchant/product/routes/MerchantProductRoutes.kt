package org.example.merchant.product.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.merchant.product.model.MerchantProductCreateRequest
import org.example.merchant.product.model.MerchantProductDeleteRequest
import org.example.merchant.product.model.MerchantProductGetQuery
import org.example.merchant.product.model.MerchantProductListQuery
import org.example.merchant.product.model.MerchantProductUpdateRequest
import org.example.service.MerchantProductService

fun Route.registerMerchantProductRoutes(
    merchantProductService: MerchantProductService
) {
    get("/api/v1/merchant/products") {
        val accountId = call.request.queryParameters["accountId"]
        if (accountId.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                buildJsonObject { put("error", "accountId is required") }
            )
            return@get
        }

        val productId = call.request.queryParameters["productId"]
        if (productId.isNullOrBlank()) {
            val response = merchantProductService.listProducts(
                MerchantProductListQuery(
                    accountId = accountId,
                    pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull(),
                    pageToken = call.request.queryParameters["pageToken"]
                )
            )
            call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
            return@get
        }

        val response = merchantProductService.getProduct(
            MerchantProductGetQuery(
                accountId = accountId,
                productId = productId
            )
        )
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }

    post("/api/v1/merchant/product-inputs") {
        val request = call.receive<MerchantProductCreateRequest>()
        val response = merchantProductService.createProduct(request)
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }

    patch("/api/v1/merchant/product-inputs") {
        val request = call.receive<MerchantProductUpdateRequest>()
        val response = merchantProductService.updateProduct(request)
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }

    delete("/api/v1/merchant/product-inputs") {
        val request = call.receive<MerchantProductDeleteRequest>()
        val response = merchantProductService.deleteProduct(request)
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }
}
