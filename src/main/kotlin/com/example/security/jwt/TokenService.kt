package com.example.security.jwt

import com.example.data.model.jwt.TokenClaim
import com.example.data.model.jwt.TokenConfig

interface TokenService {
    fun generateToken(config:TokenConfig,vararg claims : TokenClaim):String
}