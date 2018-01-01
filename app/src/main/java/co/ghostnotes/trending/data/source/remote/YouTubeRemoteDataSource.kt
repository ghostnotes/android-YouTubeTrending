package co.ghostnotes.trending.data.source.remote

import android.util.Log
import co.ghostnotes.trending.data.VideoData
import co.ghostnotes.trending.data.source.YouTubeDataSource
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import io.reactivex.Observable

class YouTubeRemoteDataSource(private val googleAccountCredential: GoogleAccountCredential): YouTubeDataSource {

    override fun getTrendingVideos(accountName: String, regionCode: String, maxResultsNumber: Long): Observable<MutableList<VideoData>> {
        return Observable.create({
            try {
                val youtube = createYouTube(accountName)

                val result = youtube.videos().list("snippet,contentDetails,statistics")
                        .setChart(YOUTUBE_CHART_MOST_POPULAR)
                        .setRegionCode(regionCode)
                        .setMaxResults(maxResultsNumber)
                        .execute()

                val videos = result.items
                val videoDataList = mutableListOf<VideoData>()
                videos?.forEach {
                    Log.d("TEST", it.snippet.title)
                    videoDataList.add(VideoData(it))
                }

                it.onNext(videoDataList)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        })
    }

    private fun createYouTube(accountName: String): YouTube {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        googleAccountCredential.selectedAccountName = accountName

        return YouTube.Builder(transport, jsonFactory, googleAccountCredential)
                .setApplicationName(YOUTUBE_APPLICATION_NAME).build()
    }

    companion object {
        private const val YOUTUBE_APPLICATION_NAME = "YouTube Trending"
        private const val YOUTUBE_CHART_MOST_POPULAR = "mostPopular"
    }

}