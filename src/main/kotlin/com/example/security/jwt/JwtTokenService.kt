package com.example.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.jwt.TokenClaim
import com.example.data.model.jwt.TokenConfig
import java.util.*

class JwtTokenService :TokenService  {
    override fun generateToken(config: TokenConfig, vararg claims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() +config.expiresIn))
        //we can add multiple claims
        claims.forEach {
            token = token.withClaim(it.name,it.value)
        }
        return token.sign(Algorithm.HMAC256(config.secret))
    }
}