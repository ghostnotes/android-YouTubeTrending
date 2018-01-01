package co.ghostnotes.trending.main

import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import co.ghostnotes.trending.core.executor.PostExecutionThread
import co.ghostnotes.trending.core.executor.ThreadExecutor
import co.ghostnotes.trending.data.VideoData
import co.ghostnotes.trending.data.source.YouTubeDataSource
import co.ghostnotes.trending.navigation.Navigator
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTubeScopes
import javax.inject.Inject

class MainPresenter @Inject constructor(private val view: MainContract.View): MainContract.Presenter {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var youtubeDataSource: YouTubeDataSource
    @Inject
    lateinit var threadExecutor: ThreadExecutor
    @Inject
    lateinit var postExecutionThread: PostExecutionThread

    private lateinit var googleAccountCredential: GoogleAccountCredential

    private fun initializeGoogleAccount() {
        googleAccountCredential = GoogleAccountCredential
                .usingOAuth2(view.getContext(), SCOPES)
                .setBackOff(ExponentialBackOff())
    }

    /*
    private fun getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices()
        } else if (mCredential!!.selectedAccountName == null) {
            chooseGoogleAccount()
        } else if (!networkDetail.isDeviceOnline()) {
            mOutputText.text = "No network connection available."
        } else {
            MakeRequestTask(mCredential!!).execute()
        }
    }
    */
    override fun start() {
        view.hideProgressSpinner()

        initializeGoogleAccount()
        view.chooseGoogleAccount()

        // Check Google Service.

        // Check permission.

        // Choose Google Account.

        // Get trending videos.
    }

    override fun stop() {
    }

    override fun setSelectedAccountName(accountName: String) {
        googleAccountCredential.selectedAccountName = accountName
    }

    override fun newChooseAccountIntent(): Intent {
        return googleAccountCredential.newChooseAccountIntent()
    }

    override fun getTrendingVideos() {
        GetTrendingVideosTask(view, googleAccountCredential).execute()
    }

    override fun startVideoData(videoData: VideoData) {
        navigator.navigateToYouTube(view.getActivity(), videoData)
    }

    internal class GetTrendingVideosTask(private val view: MainContract.View, credential: GoogleAccountCredential) : AsyncTask<Void, Void, MutableList<VideoData>>() {

        private var youtube: com.google.api.services.youtube.YouTube

        override fun onPreExecute() {
            view.showProgressSpinner()
        }

        override fun doInBackground(vararg params: Void?): MutableList<VideoData>? {
            return try {
                getTrending()
            } catch (e: UserRecoverableAuthIOException) {
                view.requestGoogleAuthorization(e)
                cancel(true)
                null
            }
        }

        override fun onPostExecute(result: MutableList<VideoData>?) {
            view.setVideoData(result ?: mutableListOf())
            view.hideProgressSpinner()
        }

        private fun getTrending(): MutableList<VideoData> {
            val result = youtube.videos().list("snippet,contentDetails,statistics")
                    .setChart(YOUTUBE_CHART_MOST_POPULAR)
                    .setRegionCode(REGION_CODE_JAPAN)
                    .setMaxResults(MAX_RESULTS_NUMBER)
                    .execute()

            val videos = result.items
            val videoDataList = mutableListOf<VideoData>()
            videos?.forEach {
                Log.d("TEST", it.snippet.title)
                videoDataList.add(VideoData(it))
            }

            return videoDataList
        }

        init {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()

            youtube = com.google.api.services.youtube.YouTube.Builder(transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart").build()
        }

        companion object {
            private const val YOUTUBE_CHART_MOST_POPULAR = "mostPopular"
            private const val MAX_RESULTS_NUMBER = 25L
            private const val REGION_CODE_JAPAN = "JP"
        }
    }

    companion object {
        private val SCOPES = listOf(YouTubeScopes.YOUTUBE_READONLY)
    }

}