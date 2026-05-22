package fr.taoufikcode.presentation.details

import fr.taoufikcode.common.DateFormatters.toDisplayFormat
import fr.taoufikcode.domain.model.smartphone.SmartphoneDetails

internal fun SmartphoneDetails.toUi(): SmartphoneDetailsUi =
    SmartphoneDetailsUi(
        id = id,
        model = model,
        imageUrl = imageUrl,
        price = price,
        constructionDate = constructionDate?.toDisplayFormat() ?: "",
        description = description,
    )
