package fr.taoufikcode.discover

import android.app.Application
import fr.taoufikcode.discover.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [AppModule::class])
class DiscoverApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<DiscoverApplication> {
            androidLogger()
            androidContext(this@DiscoverApplication)
        }
    }
}
