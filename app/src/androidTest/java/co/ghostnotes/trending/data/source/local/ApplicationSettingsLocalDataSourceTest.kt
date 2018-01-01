package co.ghostnotes.trending.data.source.local

import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApplicationSettingsLocalDataSourceTest {

    private lateinit var applicationSettingsLocalDataSource: ApplicationSettingsLocalDataSource

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        applicationSettingsLocalDataSource = ApplicationSettingsLocalDataSource(sharedPreferences)
    }

    @Test
    @Throws(Exception::class)
    fun getAccountNameAndSetAccountName() {
        // Test null
        var accountName = applicationSettingsLocalDataSource.getAccountName()
        assertThat(accountName, nullValue())

        // Input
        val expected = "test@ghostnotes.co"
        applicationSettingsLocalDataSource.setAccountName(expected)

        // Test
        accountName = applicationSettingsLocalDataSource.getAccountName()
        assertThat(accountName, `is`(expected))
    }


}