package co.ghostnotes.trending.main

import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import co.ghostnotes.trending.R
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
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainPresenter @Inject constructor(private val view: MainContract.View): MainContract.Presenter {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var googleAccountCredential: GoogleAccountCredential
    @Inject
    lateinit var youtubeDataSource: YouTubeDataSource
    @Inject
    lateinit var threadExecutor: ThreadExecutor
    @Inject
    lateinit var postExecutionThread: PostExecutionThread

    private var selectedAccountName: String? = null

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

        //initializeGoogleAccount()
        view.chooseGoogleAccount()

        // Check Google Service.

        // Check permission.

        // Choose Google Account.

        // Get trending videos.
    }

    override fun stop() {
        // Do nothing.
    }

    override fun setSelectedAccountName(accountName: String) {
        selectedAccountName = accountName
    }

    override fun newChooseAccountIntent(): Intent {
        return googleAccountCredential.newChooseAccountIntent()
    }

    override fun getTrendingVideos() {
        view.showProgressSpinner()

        youtubeDataSource.getTrendingVideos(selectedAccountName!!,REGION_CODE_JAPAN, MAX_RESULTS_NUMBER)
                .subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler())
                .subscribe(this::onTrendingVideosNext, this::onTrendingVideosError)
    }

    internal fun onTrendingVideosNext(videoDataList: MutableList<VideoData>) {
        view.setVideoData(videoDataList)
        view.hideProgressSpinner()
    }

    internal fun onTrendingVideosError(e: Throwable) {
        Log.e("TEST", e.message, e)

        if (e is UserRecoverableAuthIOException) {
            view.requestGoogleAuthorization(e)
        } else {
            view.setVideoData(mutableListOf())
            view.hideProgressSpinner()
            view.showSnackBar(R.string.error_message_failed_to_get_trending_videos)
        }
    }

    override fun startVideoData(videoData: VideoData) {
        navigator.navigateToYouTube(view.getActivity(), videoData)
    }

    companion object {
        private const val MAX_RESULTS_NUMBER = 25L
        private const val REGION_CODE_JAPAN = "JP"
    }

}