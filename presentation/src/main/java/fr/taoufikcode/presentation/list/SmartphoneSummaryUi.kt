package fr.taoufikcode.presentation.list

import androidx.compose.runtime.Immutable

@Immutable
data class SmartphoneSummaryUi(
    val id: String,
    val model: String,
    val imageUrl: String,
)
