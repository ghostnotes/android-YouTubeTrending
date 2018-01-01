package co.ghostnotes.trending.core.bus

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxBus {

    private val bus = PublishSubject.create<Event>()

    fun send(event: Event) {
        bus.onNext(event)
    }

    fun toObservable(): Observable<Event> {
        return bus
    }

    fun hasObservers(): Boolean {
        return bus.hasObservers()
    }

}