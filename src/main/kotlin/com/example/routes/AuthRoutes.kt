package com.example.routes

import com.example.data.db.user.UserDataSource
import com.example.data.model.User
import com.example.data.model.jwt.TokenClaim
import com.example.data.model.jwt.TokenConfig
import com.example.data.model.requests.AuthRequest
import com.example.data.model.response.AuthResponse
import com.example.data.model.salt.SaltedHash
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.security.hashing.HashingService
import com.example.security.jwt.TokenService

fun Route.signUp(
    hashingService: HashingService,
     userDataSource: UserDataSource
){
    post("signup") {
        val request = call.receive<AuthRequest>()
        val isEmpty = request.username.isEmpty() || request.password.isEmpty()
        if(isEmpty){

            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )

        val wasAcknowledged = userDataSource.insertUser(user)
        if(!wasAcknowledged){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK)

    }
}

fun Route.login(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService, tokenConfig: TokenConfig){
    post("login"){
        val request = call.receive<AuthRequest>()
        val isEmpty = request.username.isEmpty() || request.password.isEmpty()
        if(isEmpty){

            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val user = userDataSource.getUserByUsername(request.username)
        if(user == null ){
            call.respond(HttpStatusCode.Conflict,"Incorrect username or password")
            return@post
        }
        val isValidPassword = hashingService.verify(request.password, SaltedHash(hash = user.password, salt = user.salt))
        if(!isValidPassword ){
            call.respond(HttpStatusCode.Conflict,"Incorrect  password")
            return@post
        }
        val token = tokenService.generateToken(
            tokenConfig,TokenClaim(name = "userId", value = user.id.toString())
        )
        call.respond(AuthResponse(token = token))
    }
}

fun Route.home(){
    authenticate {
        get("home"){
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId",String::class)
            val json = """
                {
                "success":true,
                "message":"Welcome to the note app user $userId"
                }
            """.trimIndent()
            call.respond(json)
        }
    }
}