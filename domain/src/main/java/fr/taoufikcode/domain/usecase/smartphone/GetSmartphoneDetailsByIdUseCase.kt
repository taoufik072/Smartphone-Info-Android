package fr.taoufikcode.domain.usecase.smartphone

import fr.taoufikcode.domain.repository.smartphone.SmartphoneDetailsRepository
import org.koin.core.annotation.Factory

@Factory
class GetSmartphoneDetailsByIdUseCase(
    private val repository: SmartphoneDetailsRepository,
) {
    suspend operator fun invoke(id: String) = repository.getSmartphoneById(id)
}
