package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.Beer
import models.Error
import models.Response

val beerStorage = mutableListOf<Beer>()

fun Route.beerRoutes() {
    route("/beer") {
        get {
            if (beerStorage.isNotEmpty()) {
                call.respond(beerStorage)
            } else {
                call.response.status(HttpStatusCode.OK)
                call.respond(
                    Response(
                        data = listOf(
                            Beer(
                                id = 1L,
                                name = "Bock"
                            )
                        )
                    )
                )
            }
        }

        get("{id?}") {
            val id = call.parameters["id"] ?: kotlin.run {
                call.response.status(HttpStatusCode.BadRequest)
                return@get call.respond(
                    Response<Beer>(
                        errors = listOf(
                            Error(text = "Missing id")
                        )
                    )
                )
            }

            val beer = beerStorage.find { it.id.toString() == id }
                ?: kotlin.run {
                    call.response.status(HttpStatusCode.NotFound)
                    return@get call.respond(
                        Response<Beer>(
                            errors = listOf(
                                Error(text = "No beer with id $id")
                            )
                        )
                    )
                }

            call.respond(beer)
        }
    }
}
