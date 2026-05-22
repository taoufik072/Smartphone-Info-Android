package fr.taoufikcode.data.smartphones.repository

import fr.taoufikcode.common.coroutines.DispatcherProvider
import fr.taoufikcode.data.core.DataResult
import fr.taoufikcode.data.core.toDomain
import fr.taoufikcode.data.smartphones.remote.SmartphoneRemoteDataSource
import fr.taoufikcode.data.smartphones.remote.dto.toDomain
import fr.taoufikcode.domain.model.smartphone.SmartphoneDetails
import fr.taoufikcode.domain.repository.smartphone.SmartphoneDetailsRepository
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton(binds = [SmartphoneDetailsRepository::class])
class SmartphoneDetailsRepositoryImpl(
    private val remoteDataSource: SmartphoneRemoteDataSource,
    private val dispatchers: DispatcherProvider,
) : SmartphoneDetailsRepository {
    override suspend fun getSmartphoneById(id: String): Result<SmartphoneDetails> =
        withContext(dispatchers.io) {
            when (val result = remoteDataSource.getSmartphoneDetails(id)) {
                is DataResult.Success -> Result.success(result.data.toDomain())
                is DataResult.Error -> Result.failure(Exception(result.error.toDomain()))
            }
        }
}
