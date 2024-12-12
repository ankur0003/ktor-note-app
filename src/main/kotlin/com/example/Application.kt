package com.example

import com.example.data.model.jwt.TokenConfig
import com.example.data.model.salt.SaltedHash
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import io.ktor.server.application.*
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import com.example.security.hashing.SHA256HashingService
import com.example.security.jwt.JwtTokenService
import java.security.SecureRandom

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    print("vndv")
    val db = KMongo.createClient().coroutine.getDatabase("message_db")

    val jwtTokenService = JwtTokenService()
    val config = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        secret = environment.config.property("jwt.secret").getString()
    )
    val hashService = SHA256HashingService()

    configureSecurity(config)
    configureSerialization()
    configureRouting(hashService,db,jwtTokenService)
//    generateSaltedHash("123456",32)
}
 fun generateSaltedHash(value: String, saltLength: Int): SaltedHash {
    val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
     print("======Salt=====")
     println(salt)
    val saltAsHex = Hex.encodeHexString(salt)
     print("======Hex Salt=====")
     println(saltAsHex)
    val hash = DigestUtils.sha256Hex("$salt$value")
     print("======Hash=====")
     println(hash)

    return SaltedHash(
        hash =hash,
        salt = saltAsHex
    )
}
