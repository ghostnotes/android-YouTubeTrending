package co.ghostnotes.trending.main

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

internal class StateHolder(initialState: TrendingVideoState = TrendingVideoState.Initial) {

    private var state: TrendingVideoState = initialState

    private val subject = PublishSubject.create<TrendingVideoState>()

    fun toObservable(): Observable<TrendingVideoState> = subject

    fun set(state: TrendingVideoState) {

    }

}
