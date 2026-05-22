package fr.taoufikcode.data.data

object SmartphoneData {
    val homeContentsJson =
        """
        {"smartphones":[
            {"id":"1","model":"iPhone 15","imageUrl":"https://img.test/1.jpg"},
            {"id":"2","model":"Galaxy S24","imageUrl":"https://img.test/2.jpg"}
        ]}
        """.trimIndent()

    val detailsJson =
        """
        {"id":"1","model":"iPhone 15","price":999.99,
         "description":"Apple flagship","constructionDate":"2023-09-12",
         "imageUrl":"https://img.test/1.jpg"}
        """.trimIndent()
}
