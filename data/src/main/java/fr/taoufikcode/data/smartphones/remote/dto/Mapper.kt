package fr.taoufikcode.data.smartphones.remote.dto

import fr.taoufikcode.common.DateFormatters.parseApiDate
import fr.taoufikcode.data.smartphones.local.entity.SmartphoneSummaryEntity
import fr.taoufikcode.domain.model.smartphone.SmartphoneDetails

internal fun SmartphoneSummaryDto.toEntity(): SmartphoneSummaryEntity =
    SmartphoneSummaryEntity(
        id = id,
        model = model ?: "",
        imageUrl = imageUrl ?: "",
    )

internal fun SmartphoneDetailsDto.toDomain(): SmartphoneDetails =
    SmartphoneDetails(
        id = id,
        model = model ?: "",
        price = price ?: 0.0,
        description = description ?: "",
        imageUrl = imageUrl ?: "",
        constructionDate = constructionDate?.parseApiDate(),
    )
