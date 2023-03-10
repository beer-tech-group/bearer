package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Beer(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String
)
