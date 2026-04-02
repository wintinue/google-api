package org.example.app

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing
import org.example.businessprofile.account.routes.registerBusinessProfileAccountRoutes
import org.example.businessprofile.category.routes.registerCategoryRoutes
import org.example.businessprofile.location.routes.registerLocationRoutes
import org.example.config.appJson
import org.example.googleapi.routes.registerGoogleApiRoutes
import org.example.merchant.account.routes.registerMerchantAccountRoutes
import org.example.merchant.product.routes.registerMerchantProductRoutes
import org.example.oauth.routes.registerOAuthRoutes
import org.example.service.BusinessProfileService
import org.example.service.GoogleApiProxyService
import org.example.service.GoogleOAuthService
import org.example.service.MerchantAccountService
import org.example.service.MerchantProductService

fun Application.googleApiModule(
    oauthService: GoogleOAuthService,
    apiProxyService: GoogleApiProxyService,
    businessProfileService: BusinessProfileService,
    merchantAccountService: MerchantAccountService,
    merchantProductService: MerchantProductService
) {
    install(ContentNegotiation) {
        json(appJson)
    }

    routing {
        registerRootRoutes(oauthService)
        registerOAuthRoutes(oauthService)
        registerGoogleApiRoutes(apiProxyService)
        registerBusinessProfileAccountRoutes(businessProfileService)
        registerCategoryRoutes(businessProfileService)
        registerLocationRoutes(businessProfileService)
        registerMerchantAccountRoutes(merchantAccountService)
        registerMerchantProductRoutes(merchantProductService)
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}
