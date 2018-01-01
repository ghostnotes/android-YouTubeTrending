package co.ghostnotes.trending.data.source.local

import android.accounts.AccountManager
import android.content.SharedPreferences
import co.ghostnotes.trending.data.source.ApplicationSettingsDataSource

class ApplicationSettingsLocalDataSource(private val sharedPreferences: SharedPreferences): ApplicationSettingsDataSource {

    override fun getAccountName(): String? {
        return sharedPreferences.getString(PREF_KEY_ACCOUNT_NAME, null)
    }

    override fun setAccountName(accountName: String) {
        sharedPreferences.edit().putString(PREF_KEY_ACCOUNT_NAME, accountName).apply()
    }

    companion object {
        private const val PREF_KEY_ACCOUNT_NAME = AccountManager.KEY_ACCOUNT_NAME
    }

}