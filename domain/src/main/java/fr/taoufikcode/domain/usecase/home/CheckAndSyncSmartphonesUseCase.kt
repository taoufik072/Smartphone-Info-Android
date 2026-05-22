package fr.taoufikcode.domain.usecase.home

import fr.taoufikcode.domain.core.isExpired
import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class CheckAndSyncSmartphonesUseCase(
    private val repository: SmartphonesSummaryRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        val lastSync = repository.getSyncDateHomeStatus().first()
        if (!lastSync.isExpired(minutePassed = SYNC_INTERVAL_MINUTES)) {
            return Result.success(Unit)
        }
        return repository.syncHome().also { result ->
            if (result.isSuccess) {
                repository.saveSyncDateHome(System.currentTimeMillis())
            }
        }
    }

    companion object {
        private const val SYNC_INTERVAL_MINUTES = 5L
    }
}
