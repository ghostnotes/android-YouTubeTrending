package co.ghostnotes.trending.data.source.remote

import android.util.Log
import co.ghostnotes.trending.data.VideoData
import co.ghostnotes.trending.data.source.YouTubeDataSource
import io.reactivex.Observable

class YouTubeRemoteDataSource(private val youtube: com.google.api.services.youtube.YouTube): YouTubeDataSource {

    override fun getTrendingVideos(regionCode: String, maxResultsNumber: Long): Observable<MutableList<VideoData>> {
        return Observable.create({
            try {
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

    /*
    init {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        youtube = com.google.api.services.youtube.YouTube.Builder(transport, jsonFactory, credential)
                .setApplicationName("YouTube Data API Android Quickstart").build()
    }
    */

    companion object {
        private const val YOUTUBE_CHART_MOST_POPULAR = "mostPopular"
    }

}