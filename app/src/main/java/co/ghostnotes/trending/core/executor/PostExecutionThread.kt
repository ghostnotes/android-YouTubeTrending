package co.ghostnotes.trending.core.executor

import io.reactivex.Scheduler

interface PostExecutionThread {

    fun getScheduler(): Scheduler

}
