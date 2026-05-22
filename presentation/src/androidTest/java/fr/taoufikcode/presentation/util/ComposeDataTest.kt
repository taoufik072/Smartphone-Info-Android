package fr.taoufikcode.presentation.util

import fr.taoufikcode.presentation.details.SmartphoneDetailsUi
import fr.taoufikcode.presentation.list.SmartphoneSummaryUi

object ComposeDataTest {
    val summaryItem1 = SmartphoneSummaryUi(id = "id-1", model = "iPhone 15 Pro", imageUrl = "")
    val summaryItem2 = SmartphoneSummaryUi(id = "id-2", model = "Galaxy S24", imageUrl = "")

    val detailsUi = SmartphoneDetailsUi(
        id = "id-1",
        model = "iPhone 15 Pro",
        imageUrl = "",
        price = 999.99,
        constructionDate = "25 Jan 2025",
        description = "Apple flagship smartphone",
    )
}
