package fr.taoufikcode.presentation.list

import fr.taoufikcode.domain.model.home.SmartphoneSummary

internal fun SmartphoneSummary.toUi(): SmartphoneSummaryUi =
    SmartphoneSummaryUi(
        id = id,
        model = model,
        imageUrl = imageUrl,
    )
