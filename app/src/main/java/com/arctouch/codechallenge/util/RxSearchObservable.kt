package com.arctouch.codechallenge.util

import android.support.v7.widget.SearchView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object RxSearchObservable {

    fun view(searchView: android.support.v7.widget.SearchView): Observable<String> {
        val subject: PublishSubject<String> = PublishSubject.create()

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                subject.onNext(newText)
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                subject.onComplete()
                return true
            }
        })
        return subject
    }

}