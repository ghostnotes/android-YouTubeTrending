package co.ghostnotes.trending.core.di.component

import android.content.Context
import co.ghostnotes.trending.core.bus.RxBus
import co.ghostnotes.trending.core.di.module.ApplicationModule
import co.ghostnotes.trending.core.executor.PostExecutionThread
import co.ghostnotes.trending.core.executor.ThreadExecutor
import co.ghostnotes.trending.data.source.YouTubeDataSource
import co.ghostnotes.trending.navigation.Navigator
import co.ghostnotes.trending.network.NetworkDetail
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface BaseComponent {

    fun context(): Context

    fun navigator(): Navigator

    fun networkDetail(): NetworkDetail

    fun rxBus(): RxBus

    fun threadExecutor(): ThreadExecutor

    fun postExecutionThread(): PostExecutionThread

    fun googleAccountCredential(): GoogleAccountCredential

    fun youTubeDataSource(): YouTubeDataSource

}