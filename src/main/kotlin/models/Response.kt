package models

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val data: T? = null,
    val errors: List<Error>? = null
)
