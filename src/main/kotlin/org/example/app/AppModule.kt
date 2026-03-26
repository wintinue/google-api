package org.example.app

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing
import org.example.businessprofile.category.routes.registerCategoryRoutes
import org.example.config.appJson
import org.example.googleapi.routes.registerGoogleApiRoutes
import org.example.oauth.routes.registerOAuthRoutes
import org.example.service.BusinessProfileService
import org.example.service.GoogleApiProxyService
import org.example.service.GoogleOAuthService

fun Application.googleApiModule(
    oauthService: GoogleOAuthService,
    apiProxyService: GoogleApiProxyService,
    businessProfileService: BusinessProfileService
) {
    install(ContentNegotiation) {
        json(appJson)
    }

    routing {
        registerRootRoutes(oauthService)
        registerOAuthRoutes(oauthService)
        registerGoogleApiRoutes(apiProxyService)
        registerCategoryRoutes(businessProfileService)
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}
