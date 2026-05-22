package fr.taoufikcode.data.smartphones.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smartphones")
data class SmartphoneSummaryEntity(
    @PrimaryKey val id: String,
    val model: String,
    val imageUrl: String,
)
