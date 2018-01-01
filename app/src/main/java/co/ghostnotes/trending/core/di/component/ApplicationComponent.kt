package co.ghostnotes.trending.core.di.component

import co.ghostnotes.trending.core.di.module.ApplicationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent: BaseComponent