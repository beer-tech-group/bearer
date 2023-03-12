package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.Beer
import models.BeerToCreate
import models.Error
import models.Response
import kotlin.random.Random

val beerStorage = mutableListOf<Beer>()

fun Route.beerRoutes() {
    get<resources.Beer> { resource ->
        if (beerStorage.isNotEmpty()) {
            resource.name?.let {
                beerStorage.filter { it.name == resource.name }
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        call.response.status(HttpStatusCode.OK)
                        call.respond(Response(data = it))
                    } ?: kotlin.run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(
                        Response<Beer>(
                            errors = listOf(
                                Error(text = "No beers with name ${resource.name} is found!")
                            )
                        )
                    )
                }
            } ?: kotlin.run {
                call.response.status(HttpStatusCode.OK)
                call.respond(Response(data = beerStorage))
            }
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

    post<resources.Beer> {
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
            call.respond(Response(data = it))
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

    get<resources.Beer.Id> { resource ->
        val id = resource.id

        val beer = beerStorage.find { beer -> beer.id == id }
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

    delete<resources.Beer.Id> { resource ->
        val id = resource.id

        if (beerStorage.removeIf { it.id == id }) {
            call.response.status(HttpStatusCode.Accepted)
            call.respond(
                Response(
                    data = "Beer with id $id has been removed."
                )
            )
        } else {
            call.response.status(HttpStatusCode.Accepted)
            call.respond(
                Response<Beer>(
                    errors = listOf(
                        Error(text = "Beer not found.")
                    )
                )
            )
        }
    }
}
