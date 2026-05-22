package fr.taoufikcode.presentation.list

import androidx.compose.runtime.Immutable

@Immutable
data class SmartphoneListState(
    val smartphones: List<SmartphoneSummaryUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isRefreshing: Boolean = false,
) {
    val isEmpty: Boolean
        get() = smartphones.isEmpty() && !isLoading && error == null
}
