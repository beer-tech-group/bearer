package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeerToCreate(
    @SerialName("name") val name: String
)
