package co.ghostnotes.trending

import android.app.Application
import co.ghostnotes.trending.core.di.component.DaggerApplicationComponent
import co.ghostnotes.trending.core.di.component.BaseComponent
import co.ghostnotes.trending.core.di.module.ApplicationModule

class TrendingApplication: Application() {

    lateinit var component: BaseComponent
        private set

    override fun onCreate() {
        super.onCreate()

        component = createComponent()
    }

    protected open fun createComponent(): BaseComponent =
            DaggerApplicationComponent.builder()
                    .applicationModule(ApplicationModule(applicationContext))
                    .build()

}