package fr.taoufikcode.discover.di

import fr.taoufikcode.common.coroutines.DefaultDispatcherProvider
import fr.taoufikcode.common.coroutines.DispatcherProvider
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
class AppConfigModule {
    @Singleton
    fun provideDispatchers(): DispatcherProvider = DefaultDispatcherProvider()
}
