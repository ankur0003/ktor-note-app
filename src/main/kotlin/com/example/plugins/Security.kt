package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.jwt.TokenConfig
import com.example.session.MySession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureSecurity(tokenConfig: TokenConfig) {
    // Please read the jwt property from the config file if you are using EngineMain

    val jwtRealm = "ktor sample app"
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .build()
            )
            validate { credential ->
                print("Incoming Payload-------> ${credential.payload}")
                if (credential.payload.audience.contains(tokenConfig.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    routing {
    }
}
