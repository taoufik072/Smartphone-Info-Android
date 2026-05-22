package fr.taoufikcode.presentation.list

sealed interface SmartphoneListEvent {
    data class ShowError(
        val message: String,
    ) : SmartphoneListEvent
}
