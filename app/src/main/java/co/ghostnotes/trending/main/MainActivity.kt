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
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.util.Log
import android.view.View
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

    private lateinit var networkDetail: NetworkDetail

    //private var mCredential: GoogleAccountCredential? = null

    private lateinit var mOutputText: TextView
    private lateinit var mCallApiButton: Button
    private lateinit var mProgress: ProgressDialog

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: VideoDataAdapter

    internal class OnVideoRefreshListener(private val swipeRefresh: SwipeRefreshLayout): SwipeRefreshLayout.OnRefreshListener {
        override fun onRefresh() {
            Handler().postDelayed({
                swipeRefresh.isRefreshing = false
            }, 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initializeInjector()
        initializeLayout()
        //initializeGoogleAccountCredential()

        presenter.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.stop()
    }

    private fun initializeInjector() {
        networkDetail = NetworkDetail(applicationContext)

        DaggerMainComponent.builder()
                .baseComponent((application as TrendingApplication).component)
                .mainPresenterModule(MainPresenterModule(this))
                .build().inject(this)
    }

    /*
    private fun initializeGoogleAccountCredential() {
        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential
                .usingOAuth2(applicationContext, SCOPES)
                .setBackOff(ExponentialBackOff())
    }
    */

    private fun initializeLayout() {
        adapter = VideoDataAdapter(presenter)
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener(OnVideoRefreshListener(binding.swipeRefresh))
    }

    /*
    private fun initializeLayout() {
        val activityLayout = LinearLayout(this)
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        activityLayout.layoutParams = lp
        activityLayout.orientation = LinearLayout.VERTICAL
        activityLayout.setPadding(16, 16, 16, 16)

        val tlp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        mCallApiButton = Button(this)
        mCallApiButton.text = BUTTON_TEXT
        mCallApiButton.setOnClickListener {
            mCallApiButton.isEnabled = false
            mOutputText.text = ""
            getResultsFromApi()
            mCallApiButton.isEnabled = true
        }
        activityLayout.addView(mCallApiButton)

        mOutputText = TextView(this)
        mOutputText.layoutParams = tlp
        mOutputText.setPadding(16, 16, 16, 16)
        mOutputText.isVerticalScrollBarEnabled = true
        mOutputText.movementMethod = ScrollingMovementMethod()
        mOutputText.text = "Click the \'${BUTTON_TEXT}\' button to test the API."
        activityLayout.addView(mOutputText)

        mProgress = ProgressDialog(this)
        mProgress.setMessage("Calling YouTube Data API ...")

        setContentView(activityLayout)
    }
    */

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

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    override fun chooseGoogleAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            val accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null)

            if (accountName != null) {
                //mCredential!!.selectedAccountName = accountName
                //getResultsFromApi()

                // TODO
                presenter.setSelectedAccountName(accountName)
                presenter.getTrendingVideos()
            } else {
                // Start a dialog from which the user can choose an account
                //startActivityForResult(mCredential!!.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)

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
        //startActivityForResult((mLastError as UserRecoverableAuthIOException).intent, REQUEST_AUTHORIZATION)
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
                    mOutputText.text = "This app requires Google Play Services. Please install Google Play Services on your device and relaunch this app."
                } else {
                    //getResultsFromApi()

                    chooseGoogleAccount()
                }
            }
            REQUEST_ACCOUNT_PICKER -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    if (accountName != null) {
                        val settings = getPreferences(Context.MODE_PRIVATE)
                        val editor = settings.edit()
                        editor.putString(PREF_ACCOUNT_NAME, accountName)
                        editor.apply()

                        //mCredential!!.selectedAccountName = accountName
                        //getResultsFromApi()

                        // TODO
                        presenter.setSelectedAccountName(accountName)
                        presenter.getTrendingVideos()
                    }
                }
            }
            REQUEST_AUTHORIZATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    //getResultsFromApi()
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

    inner class MakeRequestTask(credential: GoogleAccountCredential) : AsyncTask<Void, Void, ArrayList<String>>() {
        private var mService: com.google.api.services.youtube.YouTube
        private var mLastError: Exception? = null

        override fun onPreExecute() {
            mOutputText.text = ""
            mProgress.show()
        }

        override fun doInBackground(vararg params: Void?): ArrayList<String>? {
            try {
                getTrending()
            } catch (e: Exception) {
                mLastError = e
                cancel(true)
                return null
            }

            return try {
                getDataFromApi()
            } catch (e: Exception) {
                mLastError = e
                cancel(true)
                null
            }
        }

        override fun onPostExecute(output: ArrayList<String>?) {
            mProgress.hide()

            if (output == null || output.size == 0) {
                mOutputText.text = "No results returned."
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:")
                mOutputText.text = TextUtils.join("\n", output)
            }
        }

        override fun onCancelled() {
            mProgress.hide()

            if (mLastError != null) {
                when (mLastError) {
                    is GooglePlayServicesAvailabilityIOException -> {
                        showGooglePlayServicesAvailabilityErrorDialog(
                                (mLastError as GooglePlayServicesAvailabilityIOException).connectionStatusCode
                        )
                    }
                    is UserRecoverableAuthIOException -> {
                        startActivityForResult((mLastError as UserRecoverableAuthIOException).intent, REQUEST_AUTHORIZATION)
                    }
                    else -> {
                        mOutputText.text = "The following error occurred:\n" + mLastError!!.message
                    }
                }
            } else {
                mOutputText.text = "Request cancelled."
            }
        }

        private fun getTrending() {
            val result = mService.videos().list("snippet,contentDetails,statistics")
                    .setChart("mostPopular")
                    .setMaxResults(25L)
                    .setRegionCode("JP")
                    .execute()

            val videos = result.items
            videos?.forEach {
                Log.d("TEST", it.snippet.title)
            }
        }

        private fun getDataFromApi(): ArrayList<String> {
            // Get a list of up to 10 files.
            val channelInfo = ArrayList<String>()


            val result = mService.channels().list("snippet,contentDetails,statistics")
                    .setForUsername("GoogleDevelopers")
                    .execute()
            val channels = result.items

            if (channels != null) {
                val channel = channels[0]

                val channelId = channel.id
                val channelTitle = channel.snippet.title
                val viewCount = channel.statistics.viewCount

                channelInfo.add("This channel's ID is $channelId. Its title is '$channelTitle', and it has $viewCount views.")
            }
            return channelInfo
        }

        init {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()

            mService = com.google.api.services.youtube.YouTube.Builder(transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart").build()
        }
    }

    override fun getContext(): Context = applicationContext

    override fun getActivity(): Activity = this

    companion object {
        private const val REQUEST_ACCOUNT_PICKER = 1000
        private const val REQUEST_AUTHORIZATION = 1001
        private const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        private const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003

        private const val BUTTON_TEXT = "Call YouTube Data API"
        private const val PREF_ACCOUNT_NAME = "accountName"
        private val SCOPES = listOf(YouTubeScopes.YOUTUBE_READONLY)
    }

}
