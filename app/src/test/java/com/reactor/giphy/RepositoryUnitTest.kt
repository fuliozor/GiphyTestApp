package com.reactor.giphy

import com.reactor.giphy.data.Repository
import com.reactor.giphy.data.net.RetrofitGiphyService
import com.reactor.giphy.data.net.reponses.*
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert
import org.junit.Test

class RepositoryUnitTest {
    @Test
    fun correctResponse() {
        val elementsCount = 10

        val repository = Repository(CorrectService(elementsCount))

        repository.search("any", 0, 10).subscribe({
            assertNotNull(it)
            assertNotNull(it.total)
            assertNotNull(it.urls)

            assertEquals(it.total, elementsCount)
            assertEquals(it.urls.size, elementsCount)
        }, {
            Assert.fail()
        })
    }

    @Test
    fun emptyResponse() {
        val elementsCount = 0

        val repository = Repository(CorrectService(elementsCount))

        repository.search("any", 0, 10).subscribe({
            assertNotNull(it)
            assertNotNull(it.total)
            assertNotNull(it.urls)

            assertEquals(it.total, elementsCount)
            assertEquals(it.urls.size, elementsCount)
        }, {
            Assert.fail()
        })
    }

    @Test
    fun exception() {
        val repository = Repository(IncorrectService())

        repository.search("any", 0, 10).subscribe({
            Assert.fail()
        }, {
            assertNotNull(it)
        })
    }

    private class CorrectService(private val elementsCount: Int) : RetrofitGiphyService {
        override fun search(query: String, offset: Int, limit: Int, apiKey: String): Observable<SearchResponse> {
            val data = mutableListOf<Data>()
            for (i in 1..elementsCount) {
                data.add(Data(Images(GifPreview("http://example.com/$i"))))
            }

            val searchResponse = SearchResponse(data, Pagination(elementsCount, elementsCount, 0))
            return Observable.fromArray(searchResponse)
        }
    }

    private class IncorrectService : RetrofitGiphyService {
        override fun search(query: String, offset: Int, limit: Int, apiKey: String): Observable<SearchResponse> {
            return Observable.error(IllegalStateException())
        }
    }
}
