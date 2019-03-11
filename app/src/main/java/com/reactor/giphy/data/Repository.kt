package com.reactor.giphy.data

import com.reactor.giphy.data.model.Data
import com.reactor.giphy.data.net.RetrofitGiphyService
import io.reactivex.Observable

class Repository(private val service: RetrofitGiphyService) {

    fun search(query: String, offset: Int, limit: Int): Observable<Data> {
        return service.search(query, offset, limit, API_KEY)
            .map {
                val urls: MutableList<String> = mutableListOf()

                for (data in it.data) {
                    urls.add(data.images.preview.url)
                }

                Data(it.pagination.total, urls)
            }
    }

    companion object {
        private const val API_KEY: String = "kcp07ImK7yRlyzfoJkXu1d0gMIVUuGjN"
    }

}