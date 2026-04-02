package org.example

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.example.app.googleApiModule
import org.example.config.GoogleOAuthConfigProvider
import org.example.oauth.store.FileTokenStore
import org.example.service.BusinessProfileService
import org.example.service.GoogleApiProxyService
import org.example.service.GoogleOAuthService
import org.example.service.MerchantAccountService
import org.example.service.MerchantProductService

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val configProvider = GoogleOAuthConfigProvider.load()
    val tokenStore = FileTokenStore()
    val oauthService = GoogleOAuthService(configProvider, tokenStore)
    val apiProxyService = GoogleApiProxyService(oauthService)
    val businessProfileService = BusinessProfileService(apiProxyService)
    val merchantAccountService = MerchantAccountService(apiProxyService)
    val merchantProductService = MerchantProductService(apiProxyService)

    embeddedServer(Netty, port = port) {
        googleApiModule(
            oauthService = oauthService,
            apiProxyService = apiProxyService,
            businessProfileService = businessProfileService,
            merchantAccountService = merchantAccountService,
            merchantProductService = merchantProductService
        )
    }.start(wait = true)
}
