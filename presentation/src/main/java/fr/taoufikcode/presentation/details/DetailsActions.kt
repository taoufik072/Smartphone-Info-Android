package fr.taoufikcode.presentation.details

sealed interface DetailsActions {
    data object Retry : DetailsActions
}
