package co.ghostnotes.trending.main

import dagger.Module
import dagger.Provides

@Module
internal class MainPresenterModule(private val view: MainContract.View) {

    @Provides
    fun provideView(): MainContract.View {
        return view
    }

}
