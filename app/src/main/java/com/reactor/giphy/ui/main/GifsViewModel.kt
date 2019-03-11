package com.reactor.giphy.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.reactor.giphy.R
import com.reactor.giphy.data.NetworkState
import com.reactor.giphy.data.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class GifsViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private var disposable: Disposable = Disposables.disposed()
    private val repository: Repository = get()

    val data = MutableLiveData<NetworkState>()

    private var query = ""
    private var total: Int = 0

    private var images = mutableListOf<String>()

    fun search(query: String) {
        if (query.isEmpty()) {
            data.value =
                NetworkState.error(getApplication<Application>().getString(R.string.error_search_string_is_empty))
            return
        }

        this.query = query
        images.clear()
        total = 0
        loadMoreData()
    }

    fun loadMoreData() {
        if (disposable.isDisposed && (total == 0 || images.size < total)) {
            data.postValue(NetworkState.LOADING)
            disposable = repository.search(query, images.size, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    total = it.total
                    images.addAll(it.urls)
                    data.value = NetworkState.loaded(it)
                }, {
                    data.value =
                        NetworkState.error(getApplication<Application>().getString(R.string.error_loading_data))
                })
        }
    }

    companion object {
        private const val PAGE_SIZE: Int = 20
    }
}