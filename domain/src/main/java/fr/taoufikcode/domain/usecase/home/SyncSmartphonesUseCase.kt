package fr.taoufikcode.domain.usecase.home

import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import org.koin.core.annotation.Factory

@Factory
class SyncSmartphonesUseCase(
    private val smartphonesSummaryRepository: SmartphonesSummaryRepository,
) {
    suspend operator fun invoke() = smartphonesSummaryRepository.syncHome()
}
