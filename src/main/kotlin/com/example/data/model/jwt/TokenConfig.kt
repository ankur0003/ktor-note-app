package com.example.data.model.jwt

data class TokenConfig(val issuer: String, val audience: String, val expiresIn: Long = 600000, val secret: String)
