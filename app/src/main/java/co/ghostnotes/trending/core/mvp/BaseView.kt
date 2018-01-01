package co.ghostnotes.trending.core.mvp

import android.app.Activity
import android.content.Context

interface BaseView {

    fun getContext(): Context

    fun getActivity(): Activity

    fun finish()

}