import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.resources.*
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
    configureRouting()
    configureSerialization()
}