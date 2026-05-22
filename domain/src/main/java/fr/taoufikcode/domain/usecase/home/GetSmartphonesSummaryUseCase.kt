package fr.taoufikcode.domain.usecase.home

import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import org.koin.core.annotation.Factory

@Factory
class GetSmartphonesSummaryUseCase(
    private val repository: SmartphonesSummaryRepository,
) {
    operator fun invoke() = repository.observeSmartphonesList()
}
