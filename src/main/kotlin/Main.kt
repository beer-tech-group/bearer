import io.ktor.http.*
import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import models.Beer
import models.BeerToCreate
import models.Error
import models.Response
import org.slf4j.LoggerFactory
import java.io.File

fun main() {
    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("alias") {
            password = "password"
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, "password")

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("bearer")
        connector {
            port = 8080
        }

        sslConnector(
            keyStore = keyStore,
            keyAlias = "alias",
            keyStorePassword = { "password".toCharArray() },
            privateKeyPassword = { "password".toCharArray() }) {
            port = 8443
            keyStorePath = keyStoreFile
        }

        module(Application::module)
    }

    embeddedServer(
        factory = Netty,
        environment = environment
    ).start(wait = true)
}

fun Application.module() {
    install(Resources)
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = Response<Beer>(errors = cause.reasons.map { Error(it) })
            )
        }
    }
    install(RequestValidation) {
        validate<BeerToCreate> { beerToCreate ->  
            if (beerToCreate.name.length < 2) {
                ValidationResult.Invalid("Name should be at least 2 chars.")
            } else ValidationResult.Valid
        }
    }

    configureRouting()
    configureSerialization()
}