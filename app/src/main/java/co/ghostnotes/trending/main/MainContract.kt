package co.ghostnotes.trending.main

import android.content.Intent
import android.support.annotation.StringRes
import co.ghostnotes.trending.core.mvp.BasePresenter
import co.ghostnotes.trending.core.mvp.BaseView
import co.ghostnotes.trending.data.VideoData
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException

interface MainContract {

    interface View: BaseView {

        fun chooseGoogleAccount()

        fun requestGoogleAuthorization(exception: UserRecoverableAuthIOException)

        fun setVideoData(videoDataList: MutableList<VideoData>)

        fun showProgressSpinner()

        fun hideProgressSpinner()

        fun showSnackBar(@StringRes resId: Int)

        fun showToast(@StringRes resId: Int)

    }

    interface Presenter: BasePresenter {

        fun setSelectedAccountName(accountName: String)

        fun newChooseAccountIntent(): Intent

        fun getTrendingVideos()

        fun startVideoData(video: VideoData)

    }

}