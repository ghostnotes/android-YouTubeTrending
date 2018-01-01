package co.ghostnotes.trending.main

import co.ghostnotes.trending.core.di.FragmentScoped
import co.ghostnotes.trending.core.di.component.BaseComponent
import dagger.Component


@FragmentScoped
@Component(dependencies = [BaseComponent::class],
        modules = [MainPresenterModule::class])
internal interface MainComponent {

    fun inject(activity: MainActivity)

}