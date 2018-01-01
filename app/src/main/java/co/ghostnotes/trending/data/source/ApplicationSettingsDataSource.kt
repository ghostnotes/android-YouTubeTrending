package co.ghostnotes.trending.data.source

interface ApplicationSettingsDataSource {

    fun getAccountName(): String?

    fun setAccountName(accountName: String)

}