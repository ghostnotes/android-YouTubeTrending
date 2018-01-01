package co.ghostnotes.trending.data

import com.google.api.services.youtube.model.Video

data class VideoData(private val video: Video) {

    var id: String = video.id
        private set

    var title: String = video.snippet.title
        private set

    var thumbnail: String = video.snippet.thumbnails.medium.url
        private set

    var channelTitle: String = video.snippet.channelTitle
        private set

}