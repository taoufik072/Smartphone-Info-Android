package fr.taoufikcode.data.smartphones.remote

import fr.taoufikcode.data.core.DataError
import fr.taoufikcode.data.core.DataResult
import fr.taoufikcode.data.core.safeCall
import fr.taoufikcode.data.smartphones.remote.dto.HomeResponseDto
import fr.taoufikcode.data.smartphones.remote.dto.SmartphoneDetailsDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.koin.core.annotation.Singleton

@Singleton
class SmartphoneRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun getSmartphoneList(): DataResult<HomeResponseDto, DataError.Remote> =
        safeCall<HomeResponseDto> {
            httpClient.get("/home/contents")
        }

    suspend fun getSmartphoneDetails(
        id: String,
    ): DataResult<SmartphoneDetailsDto, DataError.Remote> =
        safeCall<SmartphoneDetailsDto> {
            httpClient.get("/smartphoneDetails/$id")
        }
}
