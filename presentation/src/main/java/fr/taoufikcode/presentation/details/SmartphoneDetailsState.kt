package fr.taoufikcode.presentation.details

import androidx.compose.runtime.Immutable

@Immutable
data class SmartphoneDetailsState(
    val smartphone: SmartphoneDetailsUi? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
