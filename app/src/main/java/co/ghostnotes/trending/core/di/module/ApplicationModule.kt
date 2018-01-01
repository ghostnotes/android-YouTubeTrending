package co.ghostnotes.trending.core.di.module

import android.content.Context
import co.ghostnotes.trending.core.bus.RxBus
import co.ghostnotes.trending.core.executor.JobExecutor
import co.ghostnotes.trending.core.executor.PostExecutionThread
import co.ghostnotes.trending.core.executor.ThreadExecutor
import co.ghostnotes.trending.core.executor.UIThread
import co.ghostnotes.trending.data.source.YouTubeDataSource
import co.ghostnotes.trending.data.source.remote.YouTubeRemoteDataSource
import co.ghostnotes.trending.navigation.Navigator
import co.ghostnotes.trending.network.NetworkDetail
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTubeScopes
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideNetworkDetail(context: Context): NetworkDetail {
        return NetworkDetail(context)
    }

    @Provides
    @Singleton
    fun provideNavigator(): Navigator {
        return Navigator()
    }

    @Provides
    @Singleton
    fun provideRxBus(): RxBus = RxBus()

    @Provides
    @Singleton
    fun provideThreadExecutor(): ThreadExecutor = JobExecutor()

    @Provides
    @Singleton
    fun providePostExecutionThread(): PostExecutionThread = UIThread()

    @Provides
    @Singleton
    fun provideGoogleAccountCredential(): GoogleAccountCredential {
        return GoogleAccountCredential
                .usingOAuth2(context, SCOPES)
                .setBackOff(ExponentialBackOff())
    }

    @Provides
    @Singleton
    fun provideYouTubeDataSource(credential: GoogleAccountCredential): YouTubeDataSource {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        val youtube = com.google.api.services.youtube.YouTube.Builder(transport, jsonFactory, credential)
                .setApplicationName("YouTube Trending").build()


        return YouTubeRemoteDataSource(youtube)
    }

    companion object {
        private val SCOPES = listOf(YouTubeScopes.YOUTUBE_READONLY)
    }

}