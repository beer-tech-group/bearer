import io.ktor.server.application.*
import io.ktor.server.routing.*
import routes.beerRoutes
import routes.indexRoute

fun Application.configureRouting() {
    routing {
        indexRoute()
        beerRoutes()
    }
}
