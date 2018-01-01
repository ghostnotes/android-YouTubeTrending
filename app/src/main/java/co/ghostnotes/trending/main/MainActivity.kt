package co.ghostnotes.trending.main

import android.Manifest
import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import pub.devrel.easypermissions.EasyPermissions
import android.widget.TextView
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.youtube.YouTubeScopes
import java.util.*
import android.content.Context
import pub.devrel.easypermissions.AfterPermissionGranted
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import co.ghostnotes.trending.R
import co.ghostnotes.trending.TrendingApplication
import co.ghostnotes.trending.data.VideoData
import co.ghostnotes.trending.databinding.ActivityMainBinding
import co.ghostnotes.trending.network.NetworkDetail
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainContract.View, EasyPermissions.PermissionCallbacks {

    @Inject
    lateinit var presenter: MainPresenter

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: VideoDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initializeInjector()
        initializeLayout()

        presenter.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.stop()
    }

    private fun initializeInjector() {
        DaggerMainComponent.builder()
                .baseComponent((application as TrendingApplication).component)
                .mainPresenterModule(MainPresenterModule(this))
                .build().inject(this)
    }

    private fun initializeLayout() {
        adapter = VideoDataAdapter(presenter)
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener(OnVideoRefreshListener(binding.swipeRefresh))
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    override fun chooseGoogleAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            val accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_KEY_ACCOUNT_NAME, null)

            if (accountName != null) {
                presenter.setSelectedAccountName(accountName)
                presenter.getTrendingVideos()
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(presenter.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS)
        }
    }

    override fun requestGoogleAuthorization(exception: UserRecoverableAuthIOException) {
        startActivityForResult(exception.intent, REQUEST_AUTHORIZATION)
    }

    override fun setVideoData(videoDataList: MutableList<VideoData>) {
        adapter.setVideoDataList(videoDataList)
    }

    override fun showProgressSpinner() {
        binding.progressSpinner.visibility = View.VISIBLE
    }

    override fun hideProgressSpinner() {
        binding.progressSpinner.visibility = View.GONE
    }

    override fun showSnackBar(resId: Int) {
        Snackbar.make(binding.constraintLayout, resId, Snackbar.LENGTH_SHORT).show()
    }

    override fun showToast(resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> {
                if (resultCode != Activity.RESULT_OK) {
                    showSnackBar(R.string.error_message_google_play_service_required)
                } else {
                    chooseGoogleAccount()
                }
            }
            REQUEST_ACCOUNT_PICKER -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    if (accountName != null) {
                        val settings = getPreferences(Context.MODE_PRIVATE)
                        val editor = settings.edit()
                        editor.putString(PREF_KEY_ACCOUNT_NAME, accountName)
                        editor.apply()

                        presenter.setSelectedAccountName(accountName)
                        presenter.getTrendingVideos()
                    }
                }
            }
            REQUEST_AUTHORIZATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    chooseGoogleAccount()
                }
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        // Do nothing.
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        // Do nothing.
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)

        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    private fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES)
        dialog.show()
    }

    override fun getContext(): Context = applicationContext

    override fun getActivity(): Activity = this

    internal class OnVideoRefreshListener(private val swipeRefresh: SwipeRefreshLayout): SwipeRefreshLayout.OnRefreshListener {
        override fun onRefresh() {
            Handler().postDelayed({
                swipeRefresh.isRefreshing = false
            }, 1000L)
        }
    }

    companion object {
        private const val REQUEST_ACCOUNT_PICKER = 1000
        private const val REQUEST_AUTHORIZATION = 1001
        private const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        private const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003

        private const val PREF_KEY_ACCOUNT_NAME = "co.ghostnotes.trending.PREF_KEY_ACCOUNT_NAME"
    }

}
