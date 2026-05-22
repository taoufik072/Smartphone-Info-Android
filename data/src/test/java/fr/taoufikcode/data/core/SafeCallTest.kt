package fr.taoufikcode.data.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.net.UnknownHostException

class SafeCallTest {

    @Test
    fun `when SocketTimeoutException thrown then returns REQUEST_TIMEOUT`() =
        runTest {
            val result = safeCall<Unit> { throw SocketTimeoutException("") }

            assertThat(result).isEqualTo(DataResult.Error(DataError.Remote.REQUEST_TIMEOUT))
        }

    @Test
    fun `when UnknownHostException thrown then returns NO_INTERNET`() =
        runTest {
            val result = safeCall<Unit> { throw UnknownHostException("No host") }

            assertThat(result).isEqualTo(DataResult.Error(DataError.Remote.NO_INTERNET))
        }
}
