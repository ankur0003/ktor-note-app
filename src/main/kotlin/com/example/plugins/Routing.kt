package com.example.plugins

import com.example.controller.NoteController
import com.example.data.db.note.NoteDataSourceImpl
import com.example.data.db.user.UserDataSourceImpl
import com.example.data.model.Note
import com.example.data.model.SuccessResponse
import com.example.data.model.jwt.TokenConfig
import com.example.routes.*
import com.example.utils.createJwt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import org.litote.kmongo.coroutine.CoroutineDatabase
import com.example.security.hashing.HashingService
import com.example.security.jwt.TokenService

fun Application.configureRouting(hashingService: HashingService,db:CoroutineDatabase,tokenService: TokenService) {
    val msgDataSource = NoteDataSourceImpl(db)
    val userDataSource = UserDataSourceImpl(db)
    val controller = NoteController(msgDataSource)
    val secret = environment.config.property("jwt.secret").getString()
    val myRealm = environment.config.property("jwt.realm").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    install(Resources)
//    install(Authentication)
//    {
//        jwt("auth-jwt") {
//            realm = myRealm
//            verifier(JWT.require(Algorithm.HMAC256(secret))
//                .withAudience(audience).withIssuer(issuer).build())
//
//            validate { creds ->
//                if(creds.payload.getClaim("username").asString() !=""){
//                    JWTPrincipal(creds.payload)
//                }else{
//                    null
//                }
//
//            }
//            challenge{defaultScheme, realm ->
//                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
//
//            }
//
//        }
//    }
    routing {
//        authenticate("auth-jwt") {
//            get("/home"){
//                val principal = call.principal<JWTPrincipal>()
//                val username  = principal!!.payload.getClaim("username").toString()
//                val expiry = principal.expiresAt?.time?.minus(System.currentTimeMillis())
//                call.respondText("Token is expired at $expiry")
//            }
//        }
        route("/") {
            /// Type safe routing
            get<Example> {
                call.respond("Example page is serving")
            }
            get<NestedExample> { nested ->
                call.respond("It is a nested example")
            }
            get<NestedExample.New> {
                call.respond("It is a nested example with more path segments")
            }

        }
        route("/note") {
            get("/all") {
                val response = controller.fetchNotes()
                call.respond(response)
            }
            post("/save") {
                val request = call.receive<Note>()
                if (request.body.isEmpty() || request.title.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, "Fields cannot be empty")
                    return@post
                }
                val response = """
                    {
                    "success":true,
                    "message":"Note saved successfully"
                    }
                """.trimIndent()
                controller.insertNote(request)

                call.respond(HttpStatusCode.OK, response)
//                call.respond(HttpStatusCode.OK,request)
            }
            put("/update/{note_id}") {
                val param = call.parameters["note_id"]
                val request = call.receive<Note>()
                if (param?.isEmpty()!! || request.body.isEmpty() || request.title.isEmpty()) {
                    call.respond("Fields are required")
                    return@put
                }
                try {
                    val updated = controller.update(request, param)
                    if (updated)
                        call.respond(HttpStatusCode.OK, SuccessResponse(message = "Note updated successfully"))

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }

            }
            post("/login") {
                val param = call.parameters
                val username = param["username"]
                val password = param["password"]

                try {
                    val token = createJwt(username!!)

                    if (token.isNotEmpty()) {

                        call.respond(HttpStatusCode.OK, hashMapOf("token" to token))
                    }

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.OK, e.message.toString())
                }
            }
            authenticate {
                get("/home"){
                    val principal = call.principal<JWTPrincipal>()
                    val username = principal?.getClaim("username",String::class)
                    call.respondText("welcome, $username")
                }
            }

        }
        signUp(hashingService,userDataSource)
        login(
            hashingService,
            userDataSource,
            tokenService,
            TokenConfig(issuer = issuer,audience=audience, secret = secret)
        )
        home()
    }
}
