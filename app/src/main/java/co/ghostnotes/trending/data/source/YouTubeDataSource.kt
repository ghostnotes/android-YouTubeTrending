package co.ghostnotes.trending.data.source

import co.ghostnotes.trending.data.VideoData
import io.reactivex.Observable

interface YouTubeDataSource {

    fun getTrendingVideos(accountName: String, regionCode: String, maxResultsNumber: Long): Observable<MutableList<VideoData>>

}