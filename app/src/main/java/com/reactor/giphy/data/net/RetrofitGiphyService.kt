package com.reactor.giphy.data.net

import com.reactor.giphy.data.net.reponses.SearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitGiphyService {

    @GET("/v1/gifs/search")
    fun search(@Query("q") query: String, @Query("offset") offset: Int, @Query("limit") limit: Int, @Query("api_key") apiKey: String): Observable<SearchResponse>
}
