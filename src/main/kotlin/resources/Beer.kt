package resources

import io.ktor.resources.*

@Resource("/beer")
class Beer(
    val name: String? = null
) {
    @Resource("{id}")
    class Id(val parent: Beer = Beer(), val id: Long)
}
