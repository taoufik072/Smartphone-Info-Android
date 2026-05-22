package fr.taoufikcode.discover.di

import fr.taoufikcode.data.di.LocalDataModule
import fr.taoufikcode.data.di.NetworkModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module

@Module
@ComponentScan("fr.taoufikcode.data")
class DataModule

@Module
@ComponentScan("fr.taoufikcode.presentation")
class PresentationModule

@Module
@ComponentScan("fr.taoufikcode.domain")
class DomainModule

@Module(
    includes = [
        NetworkModule::class,
        LocalDataModule::class,
        AppConfigModule::class,
        DataModule::class,
        DomainModule::class,
        PresentationModule::class,
    ],
)
@Configuration
@ComponentScan("fr.taoufikcode.discover")
class AppModule
