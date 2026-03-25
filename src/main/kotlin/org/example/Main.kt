package org.example

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.example.config.GoogleOAuthConfig
import org.example.routes.googleApiModule
import org.example.service.GoogleApiProxyService
import org.example.service.GoogleOAuthService
import org.example.store.FileTokenStore

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val config = GoogleOAuthConfig.load()
    val tokenStore = FileTokenStore()
    val oauthService = GoogleOAuthService(config, tokenStore)
    val apiProxyService = GoogleApiProxyService(oauthService)

    embeddedServer(Netty, port = port) {
        googleApiModule(
            oauthService = oauthService,
            apiProxyService = apiProxyService,
            config = config
        )
    }.start(wait = true)
}
