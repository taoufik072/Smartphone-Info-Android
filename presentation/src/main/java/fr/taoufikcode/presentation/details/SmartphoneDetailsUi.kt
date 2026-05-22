package fr.taoufikcode.presentation.details

import androidx.compose.runtime.Immutable

@Immutable
data class SmartphoneDetailsUi(
    val id: String,
    val model: String,
    val imageUrl: String,
    val price: Double,
    val constructionDate: String,
    val description: String,
)
