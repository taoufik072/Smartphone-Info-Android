package fr.taoufikcode.data.smartphones.local.entity

import fr.taoufikcode.domain.model.home.SmartphoneSummary

internal fun SmartphoneSummaryEntity.toDomain(): SmartphoneSummary =
    SmartphoneSummary(
        id = id,
        model = model,
        imageUrl = imageUrl,
    )
