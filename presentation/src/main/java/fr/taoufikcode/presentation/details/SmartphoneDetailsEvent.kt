package fr.taoufikcode.presentation.details

sealed interface SmartphoneDetailsEvent {
    data class ShowError(
        val message: String,
    ) : SmartphoneDetailsEvent
}
