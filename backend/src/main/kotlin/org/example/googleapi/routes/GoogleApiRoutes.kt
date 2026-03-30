package org.example.googleapi.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.example.googleapi.model.GoogleApiProxyRequest
import org.example.service.GoogleApiProxyService

fun Route.registerGoogleApiRoutes(
    apiProxyService: GoogleApiProxyService
) {
    post("/api/v1/google-api/call") {
        val request = call.receive<GoogleApiProxyRequest>()
        val response = apiProxyService.callApi(request)
        call.respond(HttpStatusCode.fromValue(response.statusCode), response.payload)
    }
}
