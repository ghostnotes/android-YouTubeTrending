package co.ghostnotes.trending.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import co.ghostnotes.trending.data.VideoData

class Navigator {

    fun navigateToYouTube(context: Context, videoData: VideoData) {
        val url = "https://www.youtube.com/watch?v=${videoData.id}"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

}