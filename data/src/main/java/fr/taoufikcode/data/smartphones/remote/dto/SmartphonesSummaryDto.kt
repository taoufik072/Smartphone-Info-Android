package fr.taoufikcode.data.smartphones.remote.dto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeResponseDto(
    @SerialName("smartphones") val smartphones: List<SmartphoneSummaryDto>,
)

@Serializable
data class SmartphoneSummaryDto(
    @SerialName("id") val id: String,
    @SerialName("model") val model: String?,
    @SerialName("imageUrl") val imageUrl: String?,
)
