package fr.taoufikcode.presentation.list

sealed interface ListActions {
    data class SmartphoneClick(
        val smartphoneId: String,
    ) : ListActions

    data object Refresh : ListActions
}
