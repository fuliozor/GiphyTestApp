package com.reactor.giphy.data

import com.reactor.giphy.data.model.Data

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val data: Data? = null,
    val message: String? = null
) {
    companion object {
        val LOADING = NetworkState(Status.RUNNING)
        fun loaded(data: Data) = NetworkState(Status.SUCCESS, data = data)
        fun error(msg: String?) = NetworkState(Status.FAILED, message = msg)
    }
}
