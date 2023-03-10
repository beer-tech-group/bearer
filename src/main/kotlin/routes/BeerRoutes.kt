package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.Beer
import models.BeerToCreate
import models.Error
import models.Response
import kotlin.random.Random

val beerStorage = mutableListOf<Beer>()

fun Route.beerRoutes() {
    route("/beer") {
        get {
            if (beerStorage.isNotEmpty()) {
                call.response.status(HttpStatusCode.OK)
                call.respond(Response(data = beerStorage))
            } else {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(
                    Response<Beer>(
                        errors = listOf(
                            Error(text = "No beers found!")
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

        post {
            val beer = call.receive<BeerToCreate>()
            val id = Random.nextLong()

            beerStorage.add(
                Beer(
                    id = id,
                    name = beer.name
                )
            )

            beerStorage.find { it.id == id }?.let {
                call.response.status(HttpStatusCode.Created)
                call.respond(Response(data = beer))
            } ?: kotlin.run {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(
                    Response<Beer>(
                        errors = listOf(
                            Error(text = "Cannot create the beer.")
                        )
                    )
                )
            }
        }
    }
}
