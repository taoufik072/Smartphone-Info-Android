package fr.taoufikcode.domain.repository.home

import fr.taoufikcode.domain.model.home.SmartphoneSummary
import kotlinx.coroutines.flow.Flow

interface SmartphonesSummaryRepository {
    fun observeSmartphonesList(): Flow<List<SmartphoneSummary>>

    suspend fun syncHome(): Result<Unit>

    fun getSyncDateHomeStatus(): Flow<Long>

    suspend fun saveSyncDateHome(timestamp: Long): Result<Unit>
}
