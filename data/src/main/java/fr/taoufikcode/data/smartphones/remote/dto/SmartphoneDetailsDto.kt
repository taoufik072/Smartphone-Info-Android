package fr.taoufikcode.data.smartphones.remote.dto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmartphoneDetailsDto(
    @SerialName("id") val id: String,
    @SerialName("model") val model: String?,
    @SerialName("price") val price: Double?,
    @SerialName("description") val description: String?,
    @SerialName("constructionDate") val constructionDate: String?,
    @SerialName("imageUrl") val imageUrl: String?,
)
