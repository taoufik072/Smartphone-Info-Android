package fr.taoufikcode.domain.model.smartphone

import java.time.LocalDate

data class SmartphoneDetails(
    val id: String,
    val model: String,
    val price: Double,
    val description: String,
    val constructionDate: LocalDate?,
    val imageUrl: String,
)
