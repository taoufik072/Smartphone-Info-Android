package fr.taoufikcode.discover.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object SmartphoneList : Route

    @Serializable
    data class SmartphoneDetails(
        val smartphoneId: String,
    ) : Route
}
