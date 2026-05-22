package fr.taoufikcode.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import fr.taoufikcode.data.smartphones.local.entity.SmartphoneSummaryEntity
import fr.taoufikcode.data.smartphones.local.entity.toDomain
import fr.taoufikcode.data.smartphones.remote.dto.SmartphoneDetailsDto
import fr.taoufikcode.data.smartphones.remote.dto.SmartphoneSummaryDto
import fr.taoufikcode.data.smartphones.remote.dto.toDomain
import fr.taoufikcode.data.smartphones.remote.dto.toEntity
import org.junit.Test

class DataMappersTest {
    @Test
    fun `map SmartphoneSummaryDto to Entity`() {
        // Given
        val dto =
            SmartphoneSummaryDto(
                id = "1",
                model = "iPhone",
                imageUrl = "url",
            )

        // When
        val entity = dto.toEntity()

        // Then
        assertThat(entity.id).isEqualTo(dto.id)
        assertThat(entity.model).isEqualTo(dto.model)
        assertThat(entity.imageUrl).isEqualTo(dto.imageUrl)
    }

    @Test
    fun `map SmartphoneSummaryEntity to Domain`() {
        // Given
        val entity =
            SmartphoneSummaryEntity(
                id = "1",
                model = "iPhone",
                imageUrl = "url",
            )

        // When
        val domain = entity.toDomain()

        // Then
        assertThat(domain.id).isEqualTo(entity.id)
        assertThat(domain.model).isEqualTo(entity.model)
        assertThat(domain.imageUrl).isEqualTo(entity.imageUrl)
    }

    @Test
    fun `map SmartphoneDetailsDto to Domain`() {
        // Given
        val dto =
            SmartphoneDetailsDto(
                id = "1",
                model = "iPhone",
                price = 999.0,
                description = "Desc",
                imageUrl = "url",
                constructionDate = "2023-10-10",
            )

        // When
        val domain = dto.toDomain()

        // Then
        assertThat(domain.id).isEqualTo(dto.id)
        assertThat(domain.model).isEqualTo(dto.model)
        assertThat(domain.price).isEqualTo(dto.price ?: 0.0)
        assertThat(domain.description).isEqualTo(dto.description)
        assertThat(domain.imageUrl).isEqualTo(dto.imageUrl)
        assertThat(domain.constructionDate?.year).isEqualTo(2023)
        assertThat(domain.constructionDate?.monthValue).isEqualTo(10)
        assertThat(domain.constructionDate?.dayOfMonth).isEqualTo(10)
    }
}
