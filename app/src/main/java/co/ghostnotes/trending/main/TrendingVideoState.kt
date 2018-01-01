package co.ghostnotes.trending.main

import co.ghostnotes.trending.data.VideoData

internal sealed class TrendingVideoState {

    object Initial: TrendingVideoState()

    object Loading: TrendingVideoState()

    data class LoadSuccess(val videos: MutableList<VideoData>): TrendingVideoState()

    data class LoadFailed(val e: Throwable): TrendingVideoState()

}